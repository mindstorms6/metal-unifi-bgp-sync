package com.bdawg.metalbgp.unifi.sync

import com.google.gson.Gson
import com.google.gson.JsonObject

/**
 * Merges our local metal unifi config with a bigger unifi config doc (a whole one) Overwrites the
 * bgp section on a per ASN level - so ASNs not known to metalLB will be retained in the config But
 * otherwise - it's the peers from K8s - the rest are overwritten
 */
class UnifiConfigMerger {
  companion object {
    const val PROTOCOLS = "protocols"
    const val BGP = "bgp"
  }

  fun merge(local: String, remote: String): String {
    val gson = Gson()
    return gson.toJson(
        merge(
            gson.fromJson(local, JsonObject::class.java),
            gson.fromJson(remote, JsonObject::class.java)))
  }

  fun merge(local: JsonObject, remote: JsonObject): JsonObject {
    if (!local.has(PROTOCOLS) || !local.getAsJsonObject(PROTOCOLS).has(BGP)) {
      throw IllegalStateException("Expected local to have bgp section in it")
    }

    if (!remote.has(PROTOCOLS)) {
      remote.add(PROTOCOLS, JsonObject())
    } else if (!remote.getAsJsonObject(PROTOCOLS).has(BGP)) {
      remote.getAsJsonObject(PROTOCOLS).add(BGP, JsonObject())
    }
    val remoteBgp = remote.getAsJsonObject(PROTOCOLS).getAsJsonObject(BGP)
    val localBgp = local.getAsJsonObject(PROTOCOLS).getAsJsonObject(BGP)
    localBgp.entrySet().forEach { remoteBgp.add(it.key, it.value) }
    return remote
  }
}

package com.bdawg.metalbgp.unifi.sync

import com.google.gson.Gson
import com.google.gson.JsonObject

data class UnifiConfigMergeResult(
    val merged: JsonObject,
    val mergedString: String,
    val localOriginal: JsonObject,
    val remoteOriginal: JsonObject,
    val needsUpdate: Boolean
)

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

  fun merge(local: String, remote: String): UnifiConfigMergeResult {
    val gson = Gson().newBuilder().setPrettyPrinting().disableHtmlEscaping().create()
    val localOriginal = gson.fromJson(local, JsonObject::class.java)
    val remoteOriginal = gson.fromJson(remote, JsonObject::class.java)
    val mergeResult = merge(localOriginal, remoteOriginal)
    val mergeResultJson = gson.toJson(mergeResult)
    val needsUpdate = !mergeResult.equals(remoteOriginal)
    return UnifiConfigMergeResult(
        mergeResult, mergeResultJson, localOriginal, remoteOriginal, needsUpdate)
  }

  private fun merge(local: JsonObject, remote: JsonObject): JsonObject {
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

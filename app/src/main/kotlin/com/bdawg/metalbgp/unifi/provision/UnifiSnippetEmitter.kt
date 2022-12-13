package com.bdawg.metalbgp.unifi.provision

import com.bdawg.metalbgp.fetcher.model.MetalBGPState
import com.bdawg.metalbgp.unifi.sync.UnifiConfigMerger
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import java.lang.IllegalArgumentException

class UnifiSnippetEmitter(private val metalBGPState: List<MetalBGPState>) {

  /**
   * Emit the Unifi BGP json snippet
   *
   * Looks similar to: <pre>
   * ```json
   * { "protocols": {
   *       "bgp": {
   *           "64501": {
   *               "neighbor": {
   *                   "192.168.1.10": {
   *                       "remote-as": "64500"
   *                   }
   *               },
   *               "parameters": {
   *                   "router-id": "192.168.1.1"
   *               }
   *           }
   *       }
   * } }
   * ```</pre>
   * ```
   */
  fun buildUnifiJsonSnippet(): String {
    // its fine enough to be multiple ASNs (router side) - but this is going to a specific router -
    // so all syncs must be for it
    if (!metalBGPState.all { it.routerAddress == metalBGPState.first().routerAddress }) {
      throw IllegalArgumentException(
          "Cannot create a unifi json with different router addresses - must all use same router address")
    }
    val bgp = JsonObject()
    metalBGPState.forEach {
      val routerAsn = JsonObject()
      bgp.add(it.routerASN, routerAsn)
      val neighbors = JsonObject()
      val remoteAs = JsonObject()
      remoteAs.add("remote-as", JsonPrimitive(it.metalASN))
      it.peers.forEach { it2 -> neighbors.add(it2, remoteAs) }
      routerAsn.add("neighbor", neighbors)

      val routerAsnParameters = JsonObject()
      routerAsnParameters.add("router-id", JsonPrimitive(it.routerAddress))
      routerAsn.add("parameters", routerAsnParameters)
    }

    val protocols = JsonObject()
    val root = JsonObject()

    root.add(UnifiConfigMerger.PROTOCOLS, protocols)
    protocols.add(UnifiConfigMerger.BGP, bgp)

    return Gson().toJson(root)
  }
}

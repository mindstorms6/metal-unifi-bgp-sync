package com.bdawg.metalbgp

import com.bdawg.metalbgp.fetcher.model.MetalBGPState
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive

class UnifiSnippetEmitter(val metalBGPState: MetalBGPState) {

    /**
     * Emit the Unifi BGP json snippet
     *
     * Looks similar to: <pre> { "protocols": {
     * ```
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
     * ```
     * } } </pre>
     */
    fun emit(): String {
        val neighbors = JsonObject()
        val remoteAs = JsonObject()
        remoteAs.add("remote-as", JsonPrimitive(metalBGPState.metalASN))
        metalBGPState.peers.forEach { neighbors.add(it, remoteAs) }
        val protocols = JsonObject()
        val bgp = JsonObject()
        val root = JsonObject()
        val routeAsn = JsonObject()
        root.add("protocols", protocols)
        protocols.add("bgp", bgp)
        bgp.add(metalBGPState.routerASN, routeAsn)
        routeAsn.add("neighbor", neighbors)
        val routerAsnParameters = JsonObject()
        routerAsnParameters.add("router-id", JsonPrimitive(metalBGPState.routerAddress))
        routeAsn.add("parameters", routerAsnParameters)
        return Gson().toJson(root)
    }
}

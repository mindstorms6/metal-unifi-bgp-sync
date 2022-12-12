package com.bdawg.metalbgp.fetcher.model

data class MetalBGPState(
    val routerASN: String,
    val metalASN: String,
    val peers: List<String>,
    val routerAddress: String
)

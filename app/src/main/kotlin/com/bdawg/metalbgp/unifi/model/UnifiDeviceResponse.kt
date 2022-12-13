package com.bdawg.metalbgp.unifi.model

data class UnifiDeviceResponse(val data: List<UnifiDevice>)

data class UnifiDevice(
    val ip: String,
    val mac: String,
    val name: String,
    val _id: String,
    val lan_ip: String?
)

data class ProvisionResponse(val meta: RcResponse)

data class RcResponse(val rc: String)

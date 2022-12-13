package com.bdawg.metalbgp.unifi.provision

import com.bdawg.metalbgp.MetalUnfiBgpSyncConfig
import com.bdawg.metalbgp.unifi.provision.model.ProvisionResponse
import com.bdawg.metalbgp.unifi.provision.model.UnifiDeviceResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.util.*
import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging

class UnifiProvisioner(private val config: MetalUnfiBgpSyncConfig) {
  private val logger = KotlinLogging.logger {}
  /**
   * Provisions the unifi router (aka reconcile with the unifi controller)
   *
   * This function will:
   * - Log in to the unifi controller application
   * - Look up the mac address of the router
   * - Do a force provision on the router
   *
   * TODO: support many controllers TODO: pass a map of logins + controllers => routers
   */
  suspend fun doProvision(routerIp: String) {
    // There's not an "official" API for this - so this is truly a single purpose class to call this
    // provision API
    // Login: curl -k -X POST --data '{"username": "usr", "password": "$pw"}' --header
    // 'Content-Type: application/json' -c cookie.txt https://192.168.0.185/api/auth/login
    // Get devices: curl -k -X GET --header 'Content-Type: application/json' -b cookie.txt
    // https://192.168.0.185/proxy/network/api/s/default/stat/device
    // Provision: curl -k -X POST --header 'Content-Type: application/json' -b cookie.txt
    // https://192.168.0.185/proxy/network/api/s/default/cmd/devmgr  -d '{"cmd": "force-provision",
    // "mac": "{mac}"}'
    val unifiConfig = config.unifiConfig
    val urlPrefix = "https://${unifiConfig.controllerIp}:443/"
    val networkApplicationPrefix =
        "${urlPrefix}${if (unifiConfig.unifiOsProxy) "proxy/network/" else ""}api/s/${unifiConfig.siteName}/"
    val loginUrl = "${urlPrefix}api/auth/login"

    val client =
        HttpClient(CIO) {
          engine {
            https {
              trustManager =
                  object : X509TrustManager {
                    override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) {}

                    override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) {}

                    override fun getAcceptedIssuers(): Array<X509Certificate>? = null
                  }
            }
          }
          expectSuccess = true
          install(HttpCookies)
          install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
          }
          install(DefaultRequest)
          install(ContentNegotiation) { gson() }
          defaultRequest {
            headers.appendIfNameAbsent(
                HttpHeaders.ContentType, ContentType.Application.Json.toString())
          }
        }

    try {
      val provisionResponse = runBlocking {
        val loginResponse =
            client.post(loginUrl) {
              val loginData = HashMap<String, String>()
              loginData["username"] = unifiConfig.unifiUsername
              loginData["password"] = unifiConfig.unifiPassowrd
              setBody(loginData)
              contentType(ContentType.Application.Json)
            }
        val csrf = loginResponse.headers.get("X-CSRF-Token")
        val unifiDeviceResponse: UnifiDeviceResponse =
            client.get { url(networkApplicationPrefix + "stat/device") }.body()
        val routers = unifiDeviceResponse.data.filter { it.lan_ip != null && it.lan_ip != "" }
        if (routers.size != 1) {
          throw IllegalStateException(
              "Expected to find 1 router to provision for site ${unifiConfig.siteName} but found ${routers.size} - cannot continue")
        }
        val deviceToProvision = routers.first()
        if (routerIp != deviceToProvision.lan_ip) {
          throw IllegalStateException("Found router for site != bgp router ${routerIp}")
        }
        val provisionResponse: ProvisionResponse =
            client
                .post(networkApplicationPrefix + "cmd/devmgr") {
                  val provisionData = HashMap<String, String>()
                  provisionData["cmd"] = "force-provision"
                  provisionData["mac"] = deviceToProvision.mac
                  headers.append("X-CSRF-Token", csrf!!)
                  setBody(provisionData)
                }
                .body()
        if (provisionResponse.meta.rc != "ok") {
          throw IllegalStateException("Failed to provision")
        }
      }
    } finally {
      client.close()
    }
  }
}

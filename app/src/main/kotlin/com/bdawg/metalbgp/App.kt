/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package com.bdawg.metalbgp

import com.bdawg.metalbgp.fetcher.MetalFetcher
import com.bdawg.metalbgp.unifi.UnifiProvisioner
import com.bdawg.metalbgp.unifi.UnifiSnippetEmitter
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.multiple
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging

data class MetalConfig(val speakerDaemonSetName: String, val metalNamespace: String)

data class UnifiControllerConfig(
    val controllerIp: String,
    val unifiOsProxy: Boolean,
    val unifiUsername: String,
    val unifiPassowrd: String,
    val siteName: String
)

data class MetalUnfiBgpSyncConfig(
    val kubeConfigPath: String?,
    val metalConfig: MetalConfig,
    val unifiConfig: UnifiControllerConfig,
    val dryRun: Boolean
)

class App(val args: Array<String>) {
  private val logger = KotlinLogging.logger {}

  /**
   * Run the sync job
   *
   * This will:
   * - Get the config from metal / k8s
   * - Turn it in to a unifi based config
   * - Push this to the unifi controller
   * - Initiate a provision for the router
   */
  suspend fun run() {
    val parser = ArgParser("metal-unifi-bgp-sync")
    val localAsns by
        parser
            .option(
                ArgType.String,
                shortName = "a",
                description =
                    "MetalASN - MetalBGP ASNs to sync - optional. Otherwise all discovered ASNs will be used",
                fullName = "asn")
            .multiple()
    val dryRun by
        parser
            .option(
                ArgType.Boolean,
                shortName = "d",
                description = "Turn off dry run mode [ default dry run ]")
            .default(true)
    val kubeConfigPath by
        parser
            .option(ArgType.String, shortName = "c", description = "Kube config path")
            .default(System.getenv("HOME") + "/.kube/config")
    val speakerDaemonSetName by
        parser
            .option(ArgType.String, shortName = "s", description = "Speaker daemon set name")
            .default("speaker")
    val metalNamespace by
        parser
            .option(ArgType.String, shortName = "m", description = "MetalLB namespace")
            .default("metallb-system")
    parser.parse(args)

    val config =
        MetalUnfiBgpSyncConfig(
            kubeConfigPath = kubeConfigPath,
            metalConfig =
                MetalConfig(
                    speakerDaemonSetName = speakerDaemonSetName, metalNamespace = metalNamespace),
            unifiConfig =
                UnifiControllerConfig(
                    controllerIp = "",
                    unifiOsProxy = true,
                    unifiUsername = "",
                    unifiPassowrd = "",
                    siteName = "default"),
            dryRun = dryRun)

    logger.info { "Fetching metal data from k8s" }
    val metalConfigs = MetalFetcher(config).fetch()
    logger.info { "Fetched ${metalConfigs.size} metal configs from k8s" }
    val metalConfigsByRouter = metalConfigs.groupBy { it.routerAddress }
    logger.info { "There are  ${metalConfigsByRouter.size} metal configs by router" }
    metalConfigsByRouter.forEach {
      logger.info { "Updating router at ${it.key} with new metal config" }
      val mergedUnifiJson = UnifiSnippetEmitter(it.value).buildUnifiJsonSnippet()
      logger.debug { "UnifiJson: $mergedUnifiJson" }
      // TODO: save the unifi json to controller
      UnifiProvisioner(config).doProvision(it.key)
    }
  }
}

fun main(args: Array<String>) {
  val app = App(args)
  val result = runBlocking { app.run() }
}

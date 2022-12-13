package com.bdawg.metalbgp.fetcher

import com.bdawg.metalbgp.MetalUnfiBgpSyncConfig
import com.bdawg.metalbgp.fetcher.model.MetalBGPState
import io.kubernetes.client.extended.kubectl.Kubectl
import io.kubernetes.client.openapi.ApiClient
import io.kubernetes.client.openapi.models.V1DaemonSet
import io.kubernetes.client.openapi.models.V1Pod
import io.kubernetes.client.util.ClientBuilder
import io.kubernetes.client.util.KubeConfig
import io.kubernetes.client.util.generic.GenericKubernetesApi
import io.kubernetes.client.util.generic.options.ListOptions
import io.kubernetes.client.util.labels.LabelSelector
import io.metallb.generated.models.V1beta2BGPPeer
import io.metallb.generated.models.V1beta2BGPPeerList
import java.io.FileReader
import mu.KotlinLogging

class MetalFetcher(private val config: MetalUnfiBgpSyncConfig) {
  private val logger = KotlinLogging.logger {}

  private fun provideApiClient(): ApiClient {
    return try {
      ClientBuilder.kubeconfig(
              KubeConfig.loadKubeConfig(config.kubeConfigPath?.let { FileReader(it) }))
          .build()
    } catch (e: Exception) {
      ClientBuilder.cluster().build()
    }
  }

  /**
   * Get the Kubernetes MetalLB cluster configuration
   *
   * Equivalent(ish) commands:
   *
   * SELECTOR=$(kubectl get daemonset -o yaml -n metallb-system speaker | yq '.spec.selector')
   * kubectl get pods -n metallb-system -l $SELECTOR -o yaml
   *
   * The goal is to build the details necessary to reconcile with the unifi controller
   *
   * This means we need:
   *
   * RouterASN MetalLB ASN Peers broadcasting this ASN
   *
   * There can be many of these - so we return all of them
   */
  fun fetch(): List<MetalBGPState> {
    // Get the BGPPeers
    val client = provideApiClient()
    val bgpPeersCRD: GenericKubernetesApi<V1beta2BGPPeer, V1beta2BGPPeerList> =
        GenericKubernetesApi(
            V1beta2BGPPeer::class.java,
            V1beta2BGPPeerList::class.java,
            "metallb.io",
            "v1beta2",
            "bgppeers",
            client)
    val peers = bgpPeersCRD.list()
    // Speaker daemonset
    val speakerDaemonSet =
        Kubectl.get(V1DaemonSet::class.java)
            .namespace(config.metalConfig.metalNamespace)
            .name(config.metalConfig.speakerDaemonSetName)
            .apiClient(client)
            .execute()
    val speakerLabelsForPods = speakerDaemonSet.spec!!.selector
    val podListOptions = ListOptions()
    podListOptions.labelSelector = LabelSelector.parse(speakerLabelsForPods!!).toString()
    // Speaker pods
    val speakerPods =
        Kubectl.get(V1Pod::class.java)
            .namespace(config.metalConfig.metalNamespace)
            .apiClient(client)
            .options(podListOptions)
            .execute()
    val speakerPodHostIps = speakerPods.mapNotNull { it.status!!.hostIP!! }
    // Mushed together
    val peerStates =
        peers.`object`.items
            .map { it.spec }
            .mapNotNull {
              MetalBGPState(
                  it!!.peerASN.toString(), it.myASN.toString(), speakerPodHostIps, it.peerAddress)
            }
    return peerStates
  }
}

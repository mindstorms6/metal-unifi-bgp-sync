package com.bdawg.metalbgp.fetcher

import com.bdawg.metalbgp.fetcher.model.MetalBGPState
import io.kubernetes.client.extended.kubectl.Kubectl
import io.kubernetes.client.openapi.ApiClient
import io.kubernetes.client.openapi.models.V1DaemonSet
import io.kubernetes.client.openapi.models.V1Pod
import io.kubernetes.client.util.generic.GenericKubernetesApi
import io.kubernetes.client.util.generic.options.ListOptions
import io.kubernetes.client.util.labels.LabelSelector
import io.metallb.generated.models.V1beta2BGPPeer
import io.metallb.generated.models.V1beta2BGPPeerList

class MetalFetcher {

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
    fun fetch(
        client: ApiClient,
        speakerDaemonSetNameSpace: String = "metallb-system",
        speakerDaemonSetName: String = "speaker"
    ): List<MetalBGPState> {
        // Get the BGPPeers
        val bgpPeersCRD: GenericKubernetesApi<V1beta2BGPPeer, V1beta2BGPPeerList> =
            GenericKubernetesApi(
                V1beta2BGPPeer::class.java,
                V1beta2BGPPeerList::class.java,
                "metallb.io",
                "v1beta2",
                "bgppeers",
                client
            )
        val peers = bgpPeersCRD.list()
        // Speaker daemonset
        val speakerDaemonSet =
            Kubectl.get(V1DaemonSet::class.java)
                .namespace(speakerDaemonSetNameSpace)
                .name(speakerDaemonSetName)
                .apiClient(client)
                .execute()
        val speakerLabelsForPods = speakerDaemonSet.spec!!.selector
        val podListOptions = ListOptions()
        podListOptions.labelSelector = LabelSelector.parse(speakerLabelsForPods!!).toString()
        // Speaker pods
        val speakerPods =
            Kubectl.get(V1Pod::class.java)
                .namespace(speakerDaemonSetNameSpace)
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
                        it!!.peerASN.toString(),
                        it.myASN.toString(),
                        speakerPodHostIps,
                        it.peerAddress
                    )
                }
        return peerStates
    }
}

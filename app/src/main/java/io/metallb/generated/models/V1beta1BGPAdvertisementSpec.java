/*
 * Kubernetes
 * No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
 *
 * The version of the OpenAPI document: v1.21.1
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

package io.metallb.generated.models;

import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** BGPAdvertisementSpec defines the desired state of BGPAdvertisement. */
@ApiModel(description = "BGPAdvertisementSpec defines the desired state of BGPAdvertisement.")
@javax.annotation.Generated(
    value = "org.openapitools.codegen.languages.JavaClientCodegen",
    date = "2022-12-12T06:38:05.495Z[Etc/UTC]")
public class V1beta1BGPAdvertisementSpec {
  public static final String SERIALIZED_NAME_AGGREGATION_LENGTH = "aggregationLength";

  @SerializedName(SERIALIZED_NAME_AGGREGATION_LENGTH)
  private Integer aggregationLength;

  public static final String SERIALIZED_NAME_AGGREGATION_LENGTH_V6 = "aggregationLengthV6";

  @SerializedName(SERIALIZED_NAME_AGGREGATION_LENGTH_V6)
  private Integer aggregationLengthV6;

  public static final String SERIALIZED_NAME_COMMUNITIES = "communities";

  @SerializedName(SERIALIZED_NAME_COMMUNITIES)
  private List<String> communities = null;

  public static final String SERIALIZED_NAME_IP_ADDRESS_POOL_SELECTORS = "ipAddressPoolSelectors";

  @SerializedName(SERIALIZED_NAME_IP_ADDRESS_POOL_SELECTORS)
  private List<V1beta2BGPPeerSpecNodeSelectors> ipAddressPoolSelectors = null;

  public static final String SERIALIZED_NAME_IP_ADDRESS_POOLS = "ipAddressPools";

  @SerializedName(SERIALIZED_NAME_IP_ADDRESS_POOLS)
  private List<String> ipAddressPools = null;

  public static final String SERIALIZED_NAME_LOCAL_PREF = "localPref";

  @SerializedName(SERIALIZED_NAME_LOCAL_PREF)
  private Integer localPref;

  public static final String SERIALIZED_NAME_NODE_SELECTORS = "nodeSelectors";

  @SerializedName(SERIALIZED_NAME_NODE_SELECTORS)
  private List<V1beta2BGPPeerSpecNodeSelectors> nodeSelectors = null;

  public static final String SERIALIZED_NAME_PEERS = "peers";

  @SerializedName(SERIALIZED_NAME_PEERS)
  private List<String> peers = null;

  public V1beta1BGPAdvertisementSpec aggregationLength(Integer aggregationLength) {

    this.aggregationLength = aggregationLength;
    return this;
  }

  /**
   * The aggregation-length advertisement option lets you “roll up” the /32s into a larger prefix.
   * Defaults to 32. Works for IPv4 addresses. minimum: 1
   *
   * @return aggregationLength
   */
  @javax.annotation.Nullable @ApiModelProperty(
      value =
          "The aggregation-length advertisement option lets you “roll up” the /32s into a larger prefix. Defaults to 32. Works for IPv4 addresses.")
  public Integer getAggregationLength() {
    return aggregationLength;
  }

  public void setAggregationLength(Integer aggregationLength) {
    this.aggregationLength = aggregationLength;
  }

  public V1beta1BGPAdvertisementSpec aggregationLengthV6(Integer aggregationLengthV6) {

    this.aggregationLengthV6 = aggregationLengthV6;
    return this;
  }

  /**
   * The aggregation-length advertisement option lets you “roll up” the /128s into a larger prefix.
   * Defaults to 128. Works for IPv6 addresses.
   *
   * @return aggregationLengthV6
   */
  @javax.annotation.Nullable @ApiModelProperty(
      value =
          "The aggregation-length advertisement option lets you “roll up” the /128s into a larger prefix. Defaults to 128. Works for IPv6 addresses.")
  public Integer getAggregationLengthV6() {
    return aggregationLengthV6;
  }

  public void setAggregationLengthV6(Integer aggregationLengthV6) {
    this.aggregationLengthV6 = aggregationLengthV6;
  }

  public V1beta1BGPAdvertisementSpec communities(List<String> communities) {

    this.communities = communities;
    return this;
  }

  public V1beta1BGPAdvertisementSpec addCommunitiesItem(String communitiesItem) {
    if (this.communities == null) {
      this.communities = new ArrayList<>();
    }
    this.communities.add(communitiesItem);
    return this;
  }

  /**
   * The BGP communities to be associated with the announcement. Each item can be a community of the
   * form 1234:1234 or the name of an alias defined in the Community CRD.
   *
   * @return communities
   */
  @javax.annotation.Nullable @ApiModelProperty(
      value =
          "The BGP communities to be associated with the announcement. Each item can be a community of the form 1234:1234 or the name of an alias defined in the Community CRD.")
  public List<String> getCommunities() {
    return communities;
  }

  public void setCommunities(List<String> communities) {
    this.communities = communities;
  }

  public V1beta1BGPAdvertisementSpec ipAddressPoolSelectors(
      List<V1beta2BGPPeerSpecNodeSelectors> ipAddressPoolSelectors) {

    this.ipAddressPoolSelectors = ipAddressPoolSelectors;
    return this;
  }

  public V1beta1BGPAdvertisementSpec addIpAddressPoolSelectorsItem(
      V1beta2BGPPeerSpecNodeSelectors ipAddressPoolSelectorsItem) {
    if (this.ipAddressPoolSelectors == null) {
      this.ipAddressPoolSelectors = new ArrayList<>();
    }
    this.ipAddressPoolSelectors.add(ipAddressPoolSelectorsItem);
    return this;
  }

  /**
   * A selector for the IPAddressPools which would get advertised via this advertisement. If no
   * IPAddressPool is selected by this or by the list, the advertisement is applied to all the
   * IPAddressPools.
   *
   * @return ipAddressPoolSelectors
   */
  @javax.annotation.Nullable @ApiModelProperty(
      value =
          "A selector for the IPAddressPools which would get advertised via this advertisement. If no IPAddressPool is selected by this or by the list, the advertisement is applied to all the IPAddressPools.")
  public List<V1beta2BGPPeerSpecNodeSelectors> getIpAddressPoolSelectors() {
    return ipAddressPoolSelectors;
  }

  public void setIpAddressPoolSelectors(
      List<V1beta2BGPPeerSpecNodeSelectors> ipAddressPoolSelectors) {
    this.ipAddressPoolSelectors = ipAddressPoolSelectors;
  }

  public V1beta1BGPAdvertisementSpec ipAddressPools(List<String> ipAddressPools) {

    this.ipAddressPools = ipAddressPools;
    return this;
  }

  public V1beta1BGPAdvertisementSpec addIpAddressPoolsItem(String ipAddressPoolsItem) {
    if (this.ipAddressPools == null) {
      this.ipAddressPools = new ArrayList<>();
    }
    this.ipAddressPools.add(ipAddressPoolsItem);
    return this;
  }

  /**
   * The list of IPAddressPools to advertise via this advertisement, selected by name.
   *
   * @return ipAddressPools
   */
  @javax.annotation.Nullable @ApiModelProperty(
      value = "The list of IPAddressPools to advertise via this advertisement, selected by name.")
  public List<String> getIpAddressPools() {
    return ipAddressPools;
  }

  public void setIpAddressPools(List<String> ipAddressPools) {
    this.ipAddressPools = ipAddressPools;
  }

  public V1beta1BGPAdvertisementSpec localPref(Integer localPref) {

    this.localPref = localPref;
    return this;
  }

  /**
   * The BGP LOCAL_PREF attribute which is used by BGP best path algorithm, Path with higher
   * localpref is preferred over one with lower localpref.
   *
   * @return localPref
   */
  @javax.annotation.Nullable @ApiModelProperty(
      value =
          "The BGP LOCAL_PREF attribute which is used by BGP best path algorithm, Path with higher localpref is preferred over one with lower localpref.")
  public Integer getLocalPref() {
    return localPref;
  }

  public void setLocalPref(Integer localPref) {
    this.localPref = localPref;
  }

  public V1beta1BGPAdvertisementSpec nodeSelectors(
      List<V1beta2BGPPeerSpecNodeSelectors> nodeSelectors) {

    this.nodeSelectors = nodeSelectors;
    return this;
  }

  public V1beta1BGPAdvertisementSpec addNodeSelectorsItem(
      V1beta2BGPPeerSpecNodeSelectors nodeSelectorsItem) {
    if (this.nodeSelectors == null) {
      this.nodeSelectors = new ArrayList<>();
    }
    this.nodeSelectors.add(nodeSelectorsItem);
    return this;
  }

  /**
   * NodeSelectors allows to limit the nodes to announce as next hops for the LoadBalancer IP. When
   * empty, all the nodes having are announced as next hops.
   *
   * @return nodeSelectors
   */
  @javax.annotation.Nullable @ApiModelProperty(
      value =
          "NodeSelectors allows to limit the nodes to announce as next hops for the LoadBalancer IP. When empty, all the nodes having  are announced as next hops.")
  public List<V1beta2BGPPeerSpecNodeSelectors> getNodeSelectors() {
    return nodeSelectors;
  }

  public void setNodeSelectors(List<V1beta2BGPPeerSpecNodeSelectors> nodeSelectors) {
    this.nodeSelectors = nodeSelectors;
  }

  public V1beta1BGPAdvertisementSpec peers(List<String> peers) {

    this.peers = peers;
    return this;
  }

  public V1beta1BGPAdvertisementSpec addPeersItem(String peersItem) {
    if (this.peers == null) {
      this.peers = new ArrayList<>();
    }
    this.peers.add(peersItem);
    return this;
  }

  /**
   * Peers limits the bgppeer to advertise the ips of the selected pools to. When empty, the
   * loadbalancer IP is announced to all the BGPPeers configured.
   *
   * @return peers
   */
  @javax.annotation.Nullable @ApiModelProperty(
      value =
          "Peers limits the bgppeer to advertise the ips of the selected pools to. When empty, the loadbalancer IP is announced to all the BGPPeers configured.")
  public List<String> getPeers() {
    return peers;
  }

  public void setPeers(List<String> peers) {
    this.peers = peers;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    V1beta1BGPAdvertisementSpec v1beta1BGPAdvertisementSpec = (V1beta1BGPAdvertisementSpec) o;
    return Objects.equals(this.aggregationLength, v1beta1BGPAdvertisementSpec.aggregationLength)
        && Objects.equals(this.aggregationLengthV6, v1beta1BGPAdvertisementSpec.aggregationLengthV6)
        && Objects.equals(this.communities, v1beta1BGPAdvertisementSpec.communities)
        && Objects.equals(
            this.ipAddressPoolSelectors, v1beta1BGPAdvertisementSpec.ipAddressPoolSelectors)
        && Objects.equals(this.ipAddressPools, v1beta1BGPAdvertisementSpec.ipAddressPools)
        && Objects.equals(this.localPref, v1beta1BGPAdvertisementSpec.localPref)
        && Objects.equals(this.nodeSelectors, v1beta1BGPAdvertisementSpec.nodeSelectors)
        && Objects.equals(this.peers, v1beta1BGPAdvertisementSpec.peers);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        aggregationLength,
        aggregationLengthV6,
        communities,
        ipAddressPoolSelectors,
        ipAddressPools,
        localPref,
        nodeSelectors,
        peers);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class V1beta1BGPAdvertisementSpec {\n");
    sb.append("    aggregationLength: ").append(toIndentedString(aggregationLength)).append("\n");
    sb.append("    aggregationLengthV6: ")
        .append(toIndentedString(aggregationLengthV6))
        .append("\n");
    sb.append("    communities: ").append(toIndentedString(communities)).append("\n");
    sb.append("    ipAddressPoolSelectors: ")
        .append(toIndentedString(ipAddressPoolSelectors))
        .append("\n");
    sb.append("    ipAddressPools: ").append(toIndentedString(ipAddressPools)).append("\n");
    sb.append("    localPref: ").append(toIndentedString(localPref)).append("\n");
    sb.append("    nodeSelectors: ").append(toIndentedString(nodeSelectors)).append("\n");
    sb.append("    peers: ").append(toIndentedString(peers)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

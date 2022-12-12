# metal-unifi-bgp-sync
Keep your unifi bgp peers in sync with metal lb speakers

This project's goal is to keep a unifi router BGP list in sync with a list of MetalLB bgp speakers

The use case I have is simple:

A semi-dynamic set of nodes run bgp speaker - and I want to keep my unifi router bgp peer settings up to date.

Unfortunately, Unifi only supports setting bgp configuration (in a persistent manner) using a file hosted on the unifi controller. To solve this, this project pulls the current unifi configuration from the controller and updates the bgp peer settings and then pushes the update version back to the controller. From there, it initiates a "provision" with the router to update it's settings.

High level flow:

1. Pull current config.gateway.json from controller
2. Pull current BGP configuration from MetalLB (and which nodes are running the metal speaker)
3. Update the config.gateway.json locally
4. If there's a diff between the controllers current state and the newly updated one, push the new config to the unifi controller
5. Ask the controller to provision the new config (if applicable)



## Dev

### Generate new models for metal:
```sh
cd $ROOT
mkdir /tmp/java
docker run \
--rm \
-v /var/run/docker.sock:/var/run/docker.sock \
-v "$(pwd)":"$(pwd)" \
-ti \
--network host \
ghcr.io/kubernetes-client/java/crd-model-gen:v1.0.6 \
/generate.sh -u https://raw.githubusercontent.com/metallb/metallb/v0.13.7/config/manifests/metallb-native.yaml -n io.metallb -p io.metallb.generated -o "$(pwd)"
cp -R /tmp/java/src/main/java/io ./app/src/main/java/
```
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

## Example as a cron

```yaml
apiVersion: batch/v1
kind: CronJob
metadata:
  name: bgpsync-job
spec:
  schedule: "0 * * * *"
  concurrencyPolicy: "Replace"
  jobTemplate:
    spec:
      ttlSecondsAfterFinished: 1800
      template:
        spec:
          serviceAccountName: bgpsync
          containers:
          - name: bgpsync
            image: ghcr.io/mindstorms6/metal-unifi-bgp-sync:main
            imagePullPolicy: Always
            env:
              - name: UNIFI_CONTROLLER_IP
                value: 192.168.X.X
              - name: UNIFI_CONTROLLER_USERNAME
                valueFrom:
                  secretKeyRef:
                    name: bgpsyncsecret
                    key: unifi-controller-username
                    optional: false
              - name: UNIFI_SSH_USERNAME
                valueFrom:
                  secretKeyRef:
                    name: bgpsyncsecret
                    key: unifi-ssh-username
                    optional: false
              - name: UNIFI_CONTROLLER_PASSWORD
                valueFrom:
                  secretKeyRef:
                    name: bgpsyncsecret
                    key: unifi-controller-password
                    optional: false
              - name: UNIFI_SSH_PASSWORD
                valueFrom:
                  secretKeyRef:
                    name: bgpsyncsecret
                    key: unifi-ssh-password
                    optional: false
              - name: DRY_RUN
                value: "true" # change as necessary
          restartPolicy: Never
---
apiVersion: v1
kind: ServiceAccount
metadata:
  name: bgpsync
---
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRole
metadata:
  # "namespace" omitted since ClusterRoles are not namespaced
  name: clusterrole:bgpsync
rules:
- apiGroups: [""]
  #
  # at the HTTP level, the name of the resource for accessing Secret
  # objects is "secrets"
  resources: ["daemonsets","pods"]
  verbs: ["get", "watch", "list"]
- apiGroups: ["apps"]
  #
  # at the HTTP level, the name of the resource for accessing Secret
  # objects is "secrets"
  resources: ["daemonsets"]
  verbs: ["get", "watch", "list"]
- apiGroups: ["metallb.io"]
  resources: ["*"]
  verbs: ["list","get"]
---
apiVersion: rbac.authorization.k8s.io/v1
# This cluster role binding allows anyone in the "manager" group to read secrets in any namespace.
kind: ClusterRoleBinding
metadata:
  name: bgpsync
subjects:
- kind: ServiceAccount
  name: bgpsync
  namespace: bgpsync
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: clusterrole:bgpsync
```

### Configuration params:

At the moment the env vars can be found in the `App.kt` file:

https://github.com/mindstorms6/metal-unifi-bgp-sync/blob/main/app/src/main/kotlin/com/bdawg/metalbgp/App.kt#L90-L155

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
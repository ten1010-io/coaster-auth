#!/usr/bin/env bash
script_path=$(dirname "$0")

if [ -z $1 ]; then
  echo "error : path of kube-apiserver.yml must be provided as argument"
  exit 1;
fi

if [ ! -f $1 ]; then
  echo "error : file with path [$1] not exist"
  exit 1;
fi

$script_path/yq -i \
'del(.spec.containers[0].command.[] | select(. == "--oidc-issuer-url=*"))
| del(.spec.containers[0].command.[] | select(. == "--oidc-client-id=*"))
| del(.spec.containers[0].command.[] | select(. == "--oidc-username-prefix=*"))
| del(.spec.containers[0].command.[] | select(. == "--oidc-groups-claim=*"))
| del(.spec.containers[0].command.[] | select(. == "--oidc-groups-prefix=*"))
| del(.spec.containers[0].command.[] | select(. == "--oidc-ca-file=*"))
| del(.spec.containers[0].volumeMounts[] | select (.name == "coaster-certs"))
| del(.spec.volumes[] | select (.name == "coaster-certs"))' \
$1

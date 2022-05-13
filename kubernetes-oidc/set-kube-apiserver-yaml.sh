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

if [ ! -f /etc/coaster/pki/ca.crt ]; then
  echo "error : file with path [/etc/coaster/pki/ca.crt] not exist"
  exit 1;
fi

$script_path/reset-kube-apiserver-yaml.sh $1

if [ $? != 0 ]; then
  echo "error : reset failed"
  exit 1;
fi

$script_path/yq -i \
'.spec.containers[0].command += "--oidc-issuer-url=https://auth.coaster.ten1010.io"
| .spec.containers[0].command += "--oidc-client-id=kubernetes"
| .spec.containers[0].command += "--oidc-username-prefix=oidc:"
| .spec.containers[0].command += "--oidc-groups-claim=groups"
| .spec.containers[0].command += "--oidc-groups-prefix=oidc:"
| .spec.containers[0].command += "--oidc-ca-file=/etc/coaster/pki/ca.crt"
| .spec.containers[0].volumeMounts += {"mountPath": "/etc/coaster/pki", "name": "coaster-certs", "readOnly": true}
| .spec.volumes += {"hostPath": {"path": "/etc/coaster/pki", "type": "DirectoryOrCreate"}, "name": "coaster-certs"}' \
$1

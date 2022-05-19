#!/usr/bin/env bash
script_path=$(dirname "$0")
output_path=${script_path}/manifests-coaster-auth

if [ -z $1 ]; then
  echo "error : pki directory path must be provided as argument"
  exit 1;
fi

if [ ! -d $1 ]; then
  echo "error : directory with path [$1] not exist"
  exit 1;
fi

if [ ! -f $1/coaster-auth.p12 ]; then
  echo "error : coaster-auth.p12 not exist in directory [$1]"
  exit 1;
fi

if [ ! -f $1/coaster-auth-jwk.key ]; then
  echo "error : coaster-auth-jwk.key not exist in directory [$1]"
  exit 1;
fi

if [ ! -f $1/coaster-auth-jwk.pub ]; then
  echo "error : coaster-auth-jwk.pub not exist in directory [$1]"
  exit 1;
fi

kubectl create secret generic pki -n coaster-auth \
--from-file=coaster-auth.p12=$1/coaster-auth.p12 \
--from-file=coaster-auth-jwk.key=$1/coaster-auth-jwk.key \
--from-file=coaster-auth-jwk.pub=$1/coaster-auth-jwk.pub \
--dry-run=client \
-o yaml \
> $output_path/secret-pki.yaml

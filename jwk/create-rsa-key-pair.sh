#!/usr/bin/env bash
output_path=/etc/coaster/pki
mkdir -p ${output_path}

if [ -e $output_path/coaster-auth-jwk.key ]; then
  echo "error : file with path [$output_path/coaster-auth-jwk.key] already exist"
  exit 1;
fi

if [ -e $output_path/coaster-auth-jwk.pub ]; then
  echo "error : file with path [$output_path/coaster-auth-jwk.pub] already exist"
  exit 1;
fi

openssl genrsa -out $output_path/keypair.pem 2048
openssl rsa -in $output_path/keypair.pem -pubout -out $output_path/coaster-auth-jwk.pub
openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in $output_path/keypair.pem -out $output_path/coaster-auth-jwk.key
rm -f $output_path/keypair.pem

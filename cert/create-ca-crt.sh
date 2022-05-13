#!/usr/bin/env bash
script_path=$(dirname "$0")
output_path=/etc/coaster/pki
mkdir -p ${output_path}

if [ -e $output_path/ca.crt ]; then
  echo "error : file with path [/etc/coaster/pki/ca.crt] already exist"
  exit 1;
fi

openssl req -config ${script_path}/ca.conf -newkey rsa -x509 -days 3650 -keyout ${output_path}/ca.key -out ${output_path}/ca.crt -set_serial 0

#!/usr/bin/env bash
script_path=$(dirname "$0")
output_path=/etc/coaster/pki

if [ -e $output_path/coaster-auth.p12 ]; then
  echo "error : file with path [/etc/coaster/pki/coaster-auth.p12] already exist"
  exit 1;
fi

openssl req -newkey rsa -config ${script_path}/coaster-auth.conf -keyout ${output_path}/coaster-auth.key -out ${output_path}/coaster-auth.csr
openssl x509 -req -CA ${output_path}/ca.crt -CAkey ${output_path}/ca.key -CAserial $output_path/.srl -CAcreateserial -in ${output_path}/coaster-auth.csr -extfile ${script_path}/coaster-auth.ext -out ${output_path}/coaster-auth.crt -days 365
openssl pkcs12 -export -nodes -in ${output_path}/coaster-auth.crt -inkey ${output_path}/coaster-auth.key -out ${output_path}/coaster-auth.p12 -passout pass: -name coaster-auth

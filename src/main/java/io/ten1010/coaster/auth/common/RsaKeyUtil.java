package io.ten1010.coaster.auth.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RsaKeyUtil {

    public static RSAPrivateKey readRsaPrivateKey(String path) {
        String trimmedKeyStr = getKeyString(path)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PRIVATE KEY-----", "");
        byte[] encoded = Base64.getDecoder().decode(trimmedKeyStr);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        try {
            return (RSAPrivateKey) getRsaKeyFactory().generatePrivate(keySpec);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public static RSAPublicKey readRsaPublicKey(String path) {
        String trimmedKeyStr = getKeyString(path)
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PUBLIC KEY-----", "");
        byte[] encoded = Base64.getDecoder().decode(trimmedKeyStr);

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        try {
            return (RSAPublicKey) getRsaKeyFactory().generatePublic(keySpec);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getKeyString(String path) {
        try {
            return Files.readString(new File(path).toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static KeyFactory getRsaKeyFactory() {
        try {
            return KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}

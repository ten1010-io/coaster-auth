package io.ten1010.coaster.auth.web.oauth2;

import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Profile("in-memory-rsa-key-source")
@Component
public class InMemoryRSAKeySource implements RSAKeySource {

    private static final RSAKey rsaKey;

    static {
        rsaKey = rsaKey();
    }

    private static KeyPair keyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static RSAKey rsaKey() {
        KeyPair keyPair = keyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        return new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .build();
    }

    @Override
    public RSAKey get() {
        return rsaKey;
    }

}

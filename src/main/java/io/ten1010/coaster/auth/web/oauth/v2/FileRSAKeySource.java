package io.ten1010.coaster.auth.web.oauth.v2;

import com.nimbusds.jose.jwk.RSAKey;
import io.ten1010.coaster.auth.common.RsaKeyUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("file-rsa-key-source")
@Component
public class FileRSAKeySource implements RSAKeySource {

    @Component
    @ConfigurationProperties(prefix = "app.oauth2.jwk")
    @Data
    public static class Oauth2JwkProperties {

        private String rsaPrivateKey;
        private String rsaPublicKey;

    }

    @Autowired
    private Oauth2JwkProperties jwkProperties;

    @Override
    public RSAKey get() {
        return new RSAKey.Builder(RsaKeyUtil.readRsaPublicKey(this.jwkProperties.getRsaPublicKey()))
                .privateKey(RsaKeyUtil.readRsaPrivateKey(this.jwkProperties.getRsaPrivateKey()))
                .build();
    }

}

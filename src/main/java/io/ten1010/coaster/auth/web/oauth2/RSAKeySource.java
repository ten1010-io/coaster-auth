package io.ten1010.coaster.auth.web.oauth2;

import com.nimbusds.jose.jwk.RSAKey;

public interface RSAKeySource {

    RSAKey get();

}

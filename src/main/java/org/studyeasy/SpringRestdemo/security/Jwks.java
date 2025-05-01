package org.studyeasy.SpringRestdemo.security;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jose.jwk.RSAKey;

@RestController
public class Jwks {

    private final RSAKey rsaKey;

    public Jwks() {
        KeyPair keyPair = KeyGeneratorUtils.generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        this.rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
    }

    @GetMapping("/oauth2/jwks")
    public Map<String, Object> getKeys() {
        return Collections.singletonMap("keys",
                Collections.singletonList(rsaKey.toPublicJWK().toJSONObject()));
    }

    public RSAKey getRsaKey() {
        return rsaKey;
    }
}

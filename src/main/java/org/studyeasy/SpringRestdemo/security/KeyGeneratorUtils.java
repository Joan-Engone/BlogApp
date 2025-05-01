package org.studyeasy.SpringRestdemo.security;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

public final class KeyGeneratorUtils {

    private KeyGeneratorUtils() {}

    public static KeyPair generateRsaKey() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            return generator.generateKeyPair();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate RSA key pair", e);
        }
    }
}

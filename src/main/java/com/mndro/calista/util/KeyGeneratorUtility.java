package com.mndro.calista.util;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

/**
 * Utility class for generating RSA key pairs.
 *
 * @since 1.0
 */
public class KeyGeneratorUtility {

    /**
     * Generates an RSA key pair with a key size of 2048 bits.
     *
     * @return A KeyPair containing the generated RSA public and private keys.
     * @throws IllegalStateException If an error occurs during key pair generation.
     */
    public static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            throw new IllegalStateException("Error generating RSA key pair", e);
        }
        return keyPair;
    }
}

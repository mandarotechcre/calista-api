package com.mndro.calista.util;

import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * A utility class for managing RSA public and private keys.
 * This class is responsible for generating and providing RSA key pairs.
 * It is designed to be used as a Spring component for easy injection
 * into other parts of the application.
 *
 * @author [Your Name]
 * @version 1.0
 */
@Component
public class RSAKeyProperties {
    /**
     * The RSA public key.
     */
    private RSAPublicKey publicKey;

    /**
     * The RSA private key.
     */
    private RSAPrivateKey privateKey;

    /**
     * Initializes a new instance of the RSAKeyProperties class.
     * It generates a new RSA key pair upon construction.
     */
    public RSAKeyProperties() {
        KeyPair pair = KeyGeneratorUtility.generateRsaKey();
        this.publicKey = (RSAPublicKey) pair.getPublic();
        this.privateKey = (RSAPrivateKey) pair.getPrivate();
    }

    /**
     * Gets the RSA public key.
     *
     * @return The RSA public key.
     */
    public RSAPublicKey getPublicKey() {
        return this.publicKey;
    }

    /**
     * Sets the RSA public key.
     *
     * @param publicKey The RSA public key to set.
     */
    public void setPublicKey(RSAPublicKey publicKey) {
        this.publicKey = publicKey;
    }

    /**
     * Gets the RSA private key.
     *
     * @return The RSA private key.
     */
    public RSAPrivateKey getPrivateKey() {
        return this.privateKey;
    }

    /**
     * Sets the RSA private key.
     *
     * @param privateKey The RSA private key to set.
     */
    public void setPrivateKey(RSAPrivateKey privateKey) {
        this.privateKey = privateKey;
    }
}

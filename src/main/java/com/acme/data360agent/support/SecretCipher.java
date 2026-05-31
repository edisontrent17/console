package com.acme.data360agent.support;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class SecretCipher {
    private static final int IV_BYTES = 12;
    private static final int TAG_BITS = 128;

    private final SecureRandom random = new SecureRandom();
    private final SecretKeySpec key;

    public SecretCipher(@Value("${app.secrets.key:}") String configuredKey) {
        this.key = configuredKey == null || configuredKey.isBlank() ? null : new SecretKeySpec(sha256(configuredKey), "AES");
    }

    public String encrypt(String secret) {
        if (secret == null || secret.isBlank()) {
            return null;
        }
        if (key == null) {
            throw new IllegalStateException("APP_SECRETS_KEY is required before saving API tokens.");
        }
        try {
            var iv = new byte[IV_BYTES];
            random.nextBytes(iv);
            var cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, iv));
            var ciphertext = cipher.doFinal(secret.getBytes(StandardCharsets.UTF_8));
            return "v1:" + Base64.getEncoder().encodeToString(iv) + ":" + Base64.getEncoder().encodeToString(ciphertext);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to encrypt secret.", e);
        }
    }

    public String decrypt(String encrypted) {
        if (encrypted == null || encrypted.isBlank()) {
            return null;
        }
        if (key == null) {
            throw new IllegalStateException("APP_SECRETS_KEY is required before reading saved API tokens.");
        }
        try {
            var parts = encrypted.split(":", 3);
            if (parts.length != 3 || !"v1".equals(parts[0])) {
                throw new IllegalArgumentException("Unsupported secret format.");
            }
            var iv = Base64.getDecoder().decode(parts[1]);
            var ciphertext = Base64.getDecoder().decode(parts[2]);
            var cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, iv));
            return new String(cipher.doFinal(ciphertext), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to decrypt secret.", e);
        }
    }

    private byte[] sha256(String value) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is unavailable.", e);
        }
    }
}

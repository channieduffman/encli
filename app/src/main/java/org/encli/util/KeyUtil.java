package org.encli.util;

import java.security.NoSuchAlgorithmException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.encli.exception.CryptoException;

public class KeyUtil {
    private static final String ALGORITHM = "AES";
    private static final int SIZE = 128;

    public static SecretKey getKey() {
        try {
            KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM);
            kg.init(SIZE);
            return kg.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException("Failed to generate AES key", e);
        }
    }
}

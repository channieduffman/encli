package org.encli.util;

import javax.crypto.*;

public class KeyUtil {
    private static final String ALGORITHM = "AES";
    private static final int SIZE = 128;

    public static SecretKey getKey() {
        SecretKey sk = null;

        try {
            KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM);
            kg.init(SIZE);
            sk = kg.generateKey();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sk;
    }
}

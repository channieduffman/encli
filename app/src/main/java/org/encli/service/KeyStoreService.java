package org.encli.service;

import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.security.KeyStore.*;
import javax.crypto.*;

import org.encli.util.*;

public class KeyStoreService {
    private static final String TYPE = "PKCS12";

    /**
     * Method to be called when KeyStore exists. Loads KeyStore and searches
     * for key given alias and password.
     * 
     * @param path
     * @param alias
     * @param password
     * @return SecretKey
     */
    private static SecretKey loadKey(Path path, String alias, char[] password) {
        SecretKey sk = null;
        KeyStore ks = null;

        try (InputStream is = Files.newInputStream(path)) {
            ks = KeyStore.getInstance(TYPE);
            ks.load(is, password);
            ProtectionParameter pp = new PasswordProtection(password);
            SecretKeyEntry skEntry = (SecretKeyEntry) ks.getEntry(alias, pp);
            sk = skEntry.getSecretKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sk;
    }

    /**
     * Method to be called when KeyStore does not exist. Loads an empty KeyStore,
     * gets a new key, and stores that key in the KeyStore.
     * 
     * @param path
     * @param alias
     * @param password
     * @return SecretKey
     */
    private static SecretKey loadNewKey(Path path, String alias, char[] password) {
        SecretKey sk = null;
        KeyStore ks = null;

        try (OutputStream os = Files.newOutputStream(path)) {
            ks = KeyStore.getInstance(TYPE);
            ks.load(null, null);
            sk = KeyUtil.getKey();
            SecretKeyEntry skEntry = new SecretKeyEntry(sk);
            ProtectionParameter pp = new PasswordProtection(password);
            ks.setEntry(alias, skEntry, pp);
            ks.store(os, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sk;
    }

    /**
     * Method determines which of the two private methods to call.
     * 
     * @param path
     * @param alias
     * @param password
     * @return
     */
    public static SecretKey getKey(Path path, String alias, char[] password) {
        SecretKey sk = null;
        try {
            boolean created = new File(path.toString()).createNewFile();
            if (created) {
                sk = loadNewKey(path, alias, password);
            } else {
                sk = loadKey(path, alias, password);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sk;
    }

}

package org.encli.service;

import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.security.KeyStore.*;
import java.security.cert.CertificateException;

import javax.crypto.*;

import org.encli.exception.CryptoException;
import org.encli.exception.UserConfigurationException;
import org.encli.exception.UserFileSystemException;
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
     * @throws CryptoException if fails to load key
     */
    private static SecretKey loadKey(Path path, String alias, char[] password) {
        SecretKey sk = null;

        try (InputStream is = Files.newInputStream(path)) {
            KeyStore ks = KeyStore.getInstance(TYPE);
            ks.load(is, password);
            ProtectionParameter pp = new PasswordProtection(password);
            SecretKeyEntry skEntry = (SecretKeyEntry) ks.getEntry(alias, pp);
            sk = skEntry.getSecretKey();
        } catch (CertificateException | KeyStoreException | NoSuchAlgorithmException | IOException e) {
            throw new CryptoException("Failed to access keystore", e);
        } catch (UnrecoverableEntryException e) {
            throw new CryptoException("Failed to retrieve secret key", e);
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
     * @throws CryptoException if fails to load key
     */
    private static SecretKey loadNewKey(Path path, String alias, char[] password) {
        SecretKey sk = null;

        try (OutputStream os = Files.newOutputStream(path)) {
            KeyStore ks = KeyStore.getInstance(TYPE);
            ks.load(null, null);
            sk = KeyUtil.getKey(); // may throw CryptoException
            SecretKeyEntry skEntry = new SecretKeyEntry(sk);
            ProtectionParameter pp = new PasswordProtection(password);
            ks.setEntry(alias, skEntry, pp);
            ks.store(os, password);
        } catch (CertificateException | KeyStoreException | NoSuchAlgorithmException | IOException e) {
            throw new CryptoException("Failed to access keystore", e);
        } catch (CryptoException e) {
            throw e;
        }

        return sk;
    }

    /**
     * Method determines which of the two private methods to call.
     * 
     * @param path
     * @param alias
     * @param password
     * @return SecretKey object or null
     * @throws CryptoException            if either loadKey() or loadNewKey() fails
     * @throws UserConfigurationException if fails to load config
     * @throws UserFileSystemException    if file access issues occur
     */
    public static SecretKey getKey(Path path, String alias, char[] password) {

        if (path == null || alias == null || password == null) {
            throw new UserConfigurationException("Failed to load user configuration");
        }

        SecretKey sk = null;

        try {

            Path parent = path.getParent();
            if (!Files.exists(parent) && Files.notExists(parent))
                Files.createDirectory(parent);

            // java.io.File.createNewFile() returns true if a new file was created,
            // i.e. if and only if it did not already exist
            boolean created = new File(path.toString()).createNewFile();
            if (created) {
                sk = loadNewKey(path, alias, password);
            } else {
                sk = loadKey(path, alias, password);
            }
        } catch (IOException e) {
            throw new UserFileSystemException("Unable to access file " + path.toString(), e);
        }

        return sk;
    }

}

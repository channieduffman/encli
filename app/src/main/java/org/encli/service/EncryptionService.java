package org.encli.service;

import org.encli.exception.CryptoException;
import org.encli.exception.UserFileSystemException;
import org.encli.util.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.security.*;
import javax.crypto.*;

public class EncryptionService {
    /* Class Constants */
    private static final String EXISTS = "The file %s already exists.";
    private static final String UNKNOWN = "The file %s may already exist.";

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int BUFFER_SIZE = 1024; // bytes

    /**
     * File encryption; this method encrypts a file specified by a Path object
     * 
     * @param src path to the input file
     * @param dst path to the output (encrypted) file
     * @param key secret key used to initialize cipher
     * @throws CryptoException
     * @throws UserFileSystemException
     */
    public static void encrypt(Path src, Path dst, SecretKey key) {
        // Retrieve output path from PathUtil
        PathUtil pu = new PathUtil(src, dst, PathUtil.ENCRYPT);
        Path out = pu.getPath();

        /*
         * Avoid overwriting files;
         * Per documentation, if Files.exists(path) and Files.notExists(path) both
         * return false, the existence of the file could not be determined. Treat this
         * as existing.
         */
        if (Files.exists(out)) {
            throw new UserFileSystemException(String.format(EXISTS, out));
        } else if (!Files.exists(out) && !Files.notExists(out)) {
            throw new UserFileSystemException(String.format(UNKNOWN, out));
        }

        try (OutputStream os = Files.newOutputStream(out)) {
            // Create an initialization vector to increase randomness
            IVUtil ivu = new IVUtil();

            // Store the IV with the encrypted content
            os.write(ivu.getIV());

            // Initialize cipher
            Cipher c = Cipher.getInstance(ALGORITHM);
            c.init(Cipher.ENCRYPT_MODE, key, ivu.getIVSpec());

            // Buffer the input and write it out encrypted
            try (CipherOutputStream cos = new CipherOutputStream(os, c);
                    InputStream is = Files.newInputStream(src)) {
                byte[] buffer = new byte[BUFFER_SIZE];
                int readBytes;
                while ((readBytes = is.read(buffer)) != -1) {
                    cos.write(buffer, 0, readBytes);
                }
            }
        } catch (IOException e) {
            throw new UserFileSystemException("Failed to open, read, or write to files", e);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException | NoSuchAlgorithmException
                | NoSuchPaddingException e) {
            throw new CryptoException("Failed to initialize cipher", e);
        }
    }

    /**
     * File decryption; this method decrypts an encrypted file specified by a Path
     * object
     * 
     * @param src path to the input file
     * @param dst path to the output (decrypted) file
     * @param key secret key used to initialize cipher
     * @throws CryptoException
     * @throws UserFileSystemException
     */
    public static void decrypt(Path src, Path dst, SecretKey key) {
        PathUtil pu = new PathUtil(src, dst, PathUtil.DECRYPT);
        Path out = pu.getPath();

        // See `EncryptionService.encrypt(Path, Path, SecretKey)`
        if (Files.exists(out)) {
            throw new UserFileSystemException(String.format(EXISTS, out));
        } else if (!Files.exists(out) && !Files.notExists(out)) {
            throw new UserFileSystemException(String.format(UNKNOWN, out));
        }

        try (InputStream is = Files.newInputStream(src)) {
            IVUtil ivu = new IVUtil(is);

            Cipher c = Cipher.getInstance(ALGORITHM);
            c.init(Cipher.DECRYPT_MODE, key, ivu.getIVSpec());

            try (CipherInputStream cis = new CipherInputStream(is, c);
                    OutputStream os = Files.newOutputStream(out)) {
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead;
                while ((bytesRead = cis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }
        } catch (IOException e) {
            throw new UserFileSystemException("Failed to process files", e);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException | NoSuchAlgorithmException
                | NoSuchPaddingException e) {
            throw new CryptoException("Failed to initialize cipher", e);
        } catch (UserFileSystemException e) { // thrown by IVUtil(InputStream)
            throw e;
        }
    }
}

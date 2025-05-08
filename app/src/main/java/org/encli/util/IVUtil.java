package org.encli.util;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;

import javax.crypto.spec.IvParameterSpec;

import org.encli.exception.UserFileSystemException;

public class IVUtil {
    /* Class Constants */
    private static final int IV_SIZE = 16;

    /* Attributes */
    private byte[] iv;
    private IvParameterSpec ivSpec;

    /* Private Methods */

    /**
     * This method extracts an IV_SIZE-byte initialization vector from the given
     * input stream, assuming that the initialization vector constitutes the
     * first IV_SIZE bytes of the file.
     * 
     * @param is InputStream object from which to extract the IV
     * @return a byte array or null
     * @throws Exception
     */
    private byte[] extractIV(InputStream is) {
        byte[] _iv = new byte[IV_SIZE];
        try {
            int bytesRead = is.read(_iv);
            if (bytesRead != IV_SIZE)
                _iv = null;
        } catch (IOException e) {
            throw new UserFileSystemException("Failed to read IV from file " + is.toString(), e);
        }
        return _iv;
    }

    /* Constructors */

    /**
     * This constructs an IVUtil object with a random initialization vector. This
     * constructor is useful when encrypting a file.
     */
    public IVUtil() {
        iv = new byte[IV_SIZE];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        ivSpec = new IvParameterSpec(iv);
    }

    /**
     * This constructs an IVUtil object by extracting bytes from an encrypted file.
     * This constructor is useful when decrypting a file.
     * 
     * @param is the encrypted input stream
     */
    public IVUtil(InputStream is) {
        iv = extractIV(is);
        ivSpec = new IvParameterSpec(iv);
    }

    /* Accessors */

    public byte[] getIV() {
        return iv;
    }

    public IvParameterSpec getIVSpec() {
        return ivSpec;
    }
}

package org.encli.util;

import java.nio.file.*;

public class PathUtil {

    private Path path;

    public static final String ENCRYPT = "ENCRYPT";
    public static final String DECRYPT = "DECRYPT";

    /**
     * @param src  the src path
     * @param dst  the dst path (or null)
     * @param mode specifies the extension based on encryption or decryption
     */
    public PathUtil(Path src, Path dst, String mode) {
        if (dst != null) {
            this.path = dst;
        } else {
            switch (mode) {
                case ENCRYPT:
                    this.path = Paths.get(src.toString() + ".enc");
                    break;
                case DECRYPT:
                    this.path = Paths.get(src.toString() + ".dec");
                    break;
                default:
                    this.path = null;
            }
        }
    }

    public Path getPath() {
        return path;
    }

}

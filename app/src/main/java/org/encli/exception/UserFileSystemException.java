package org.encli.exception;

public class UserFileSystemException extends RuntimeException {
    public UserFileSystemException(String message) {
        super(message);
    }

    public UserFileSystemException(String message, Throwable cause) {
        super(message, cause);
    }
}

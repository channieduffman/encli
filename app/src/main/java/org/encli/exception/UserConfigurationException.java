package org.encli.exception;

public class UserConfigurationException extends RuntimeException {
    public UserConfigurationException(String message) {
        super(message);
    }

    public UserConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}

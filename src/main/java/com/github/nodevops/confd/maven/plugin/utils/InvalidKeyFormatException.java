package com.github.nodevops.confd.maven.plugin.utils;

public class InvalidKeyFormatException extends Exception {
    public InvalidKeyFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidKeyFormatException(String message) {
        super(message);
    }
}

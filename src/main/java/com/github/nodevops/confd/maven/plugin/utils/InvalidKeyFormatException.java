package com.github.nodevops.confd.maven.plugin.utils;

/**
 * Created by pseillier on 29/12/2015.
 */
public class InvalidKeyFormatException extends Exception{
    public InvalidKeyFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidKeyFormatException(String message) {
        super(message);
    }
}

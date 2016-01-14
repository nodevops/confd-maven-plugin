package com.github.nodevops.confd.maven.plugin.utils;

public class DictionaryException extends Exception {
    public DictionaryException(String message, Throwable cause) {
        super(message, cause);
    }

    public DictionaryException(String message) {
        super(message);
    }
}

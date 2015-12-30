package com.github.nodevops.confd.maven.plugin.utils;

/**
 * Created by pseillier on 29/12/2015.
 */
public class DictionaryException extends Exception{
    public DictionaryException(String message, Throwable cause) {
        super(message, cause);
    }

    public DictionaryException(String message) {
        super(message);
    }
}

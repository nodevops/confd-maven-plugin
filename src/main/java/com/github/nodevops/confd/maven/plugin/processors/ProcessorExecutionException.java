package com.github.nodevops.confd.maven.plugin.processors;

/**
 * Created by pseillier on 22/12/2015.
 */
public class ProcessorExecutionException extends Exception {
    public ProcessorExecutionException(String message) {
        super(message);
    }

    public ProcessorExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}

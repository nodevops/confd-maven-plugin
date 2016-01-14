package com.github.nodevops.confd.maven.plugin.processors;

public class ProcessorExecutionException extends Exception {
    public ProcessorExecutionException(String message) {
        super(message);
    }

    public ProcessorExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}

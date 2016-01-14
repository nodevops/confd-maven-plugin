package com.github.nodevops.confd.maven.plugin.processors;

public class ProcessorCreationException extends Exception {
    public ProcessorCreationException(String message) {
        super(message);
    }

    public ProcessorCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}

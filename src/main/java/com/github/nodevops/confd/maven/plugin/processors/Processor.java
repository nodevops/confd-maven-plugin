package com.github.nodevops.confd.maven.plugin.processors;

public interface Processor {
    public void process(ProcessorContext context) throws ProcessorExecutionException;
}

package com.github.nodevops.confd.maven.plugin.processors;

import java.io.File;

public class ProcessorContextBuilder {
    private ProcessorContext processorContext;

    public ProcessorContextBuilder() {
        processorContext = new ProcessorContext();
    }

    public ProcessorContextBuilder dictionaryPath(File dictionaryPath) {
        processorContext.setDictionaryPath(dictionaryPath);
        return this;
    }

    public ProcessorContextBuilder workingDirectory(File workingDirectory) {
        processorContext.setWorkingDirectory(workingDirectory);
        return this;
    }

    public ProcessorContextBuilder encoding(String encoding) {
        processorContext.setEncoding(encoding);
        return this;
    }

    public ProcessorContext build() {
        return processorContext;
    }

}

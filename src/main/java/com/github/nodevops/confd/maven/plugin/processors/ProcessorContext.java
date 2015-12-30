package com.github.nodevops.confd.maven.plugin.processors;

import java.io.File;

/**
 * Created by pseillier on 28/12/2015.
 */
public class ProcessorContext {
    private File dictionaryPath;
    private File workingDirectory;
    private String encoding;

    /* package */ ProcessorContext() {

    }

    public File getDictionaryPath() {
        return dictionaryPath;
    }

    public void setDictionaryPath(File dictionaryPath) {
        this.dictionaryPath = dictionaryPath;
    }

    public File getWorkingDirectory() {
        return workingDirectory;
    }

    public void setWorkingDirectory(File workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getEncoding() {
        return encoding;
    }
}

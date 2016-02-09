package com.github.nodevops.confd.maven.plugin.model;

import java.io.File;

import lombok.Data;

@Data
public class TemplateConfig {
    public static final String DEFAULT_ID = "undef";
    private String id = DEFAULT_ID;
    private File src;
    private String dest;
    private boolean forceDestToLocalFileSystemType = false;
    private String[] keys;

    public String getResolvedDestPath() {
        if (forceDestToLocalFileSystemType) {
            File destAsFile = new File(dest);
            return destAsFile.getPath();
        } else {
            return dest;
        }
    }
}

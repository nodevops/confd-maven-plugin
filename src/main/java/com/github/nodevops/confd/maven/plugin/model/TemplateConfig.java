package com.github.nodevops.confd.maven.plugin.model;

import java.io.File;
import java.util.UUID;

import lombok.Data;

@Data
public class TemplateConfig {
    private String id = UUID.randomUUID().toString();
    private File src;
    private String dest;
    private String[] keys;

    public String getResolvedDestPath(boolean forceDestToLocalFileSystemType) {
        if (forceDestToLocalFileSystemType) {
            File destAsFile = new File(dest);
            return destAsFile.getPath();
        } else {
            return dest;
        }
    }
}

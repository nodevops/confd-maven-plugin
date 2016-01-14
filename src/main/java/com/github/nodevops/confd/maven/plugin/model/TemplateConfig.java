package com.github.nodevops.confd.maven.plugin.model;

import java.io.File;

import lombok.Data;

@Data
public class TemplateConfig {
    public static final String DEFAULT_ID = "undef";
    private String id = DEFAULT_ID;
    private File src;
    private File dest;
    private String[] keys;
}

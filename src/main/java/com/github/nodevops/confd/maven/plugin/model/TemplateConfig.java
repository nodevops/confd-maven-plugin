package com.github.nodevops.confd.maven.plugin.model;

import lombok.Data;

import java.io.File;
import java.util.Arrays;

/**
 * Created by pseillier on 21/12/2015.
 */
@Data
public class TemplateConfig {
    public static final String DEFAULT_ID = "undef";
    private String id = DEFAULT_ID;
    private File src;
    private File dest;
    private String[] keys;
}

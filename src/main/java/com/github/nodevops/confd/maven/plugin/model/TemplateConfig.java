package com.github.nodevops.confd.maven.plugin.model;

import java.io.File;
import java.util.Arrays;

/**
 * Created by pseillier on 21/12/2015.
 */
public class TemplateConfig {
    private File src;
    private File dest;

    private String[] keys;

    public File getSrc() {

        return src;
    }

    public File getDest() {

        return dest;
    }

    public String[] getKeys() {

        return keys;
    }

    public void setSrc(File src) {

        this.src = src;
    }

    public void setDest(File dest) {

        this.dest = dest;
    }

    public void setKeys(String[] keys) {

        this.keys = keys;
    }

    @Override
    public String toString() {
        return "TemplateConfig{" +
            "src='" + src + '\'' +
            ", dest='" + dest + '\'' +
            ", keys=" + Arrays.toString(keys) +
            '}';
    }
}

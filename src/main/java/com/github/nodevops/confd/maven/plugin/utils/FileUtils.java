package com.github.nodevops.confd.maven.plugin.utils;

import java.io.File;

public class FileUtils {

    private FileUtils() {
    }

    public static File makeAbsoluteIfNeeded(File input, File basedir) {
        if (!input.isAbsolute()) {
            return new File(basedir, input.getPath());
        } else {
            return input;
        }
    }
}

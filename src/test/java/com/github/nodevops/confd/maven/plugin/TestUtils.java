package com.github.nodevops.confd.maven.plugin;

/**
 * Created on 31/12/15.
 */
public class TestUtils {
    private TestUtils() {
    }

    public static boolean isRunningOnWindows() {
        return System.getProperty("os.name").toLowerCase().startsWith("windows");
    }
}

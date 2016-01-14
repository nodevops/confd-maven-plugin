package com.github.nodevops.confd.maven.plugin;

public class TestUtils {
    private TestUtils() {
    }

    public static boolean isRunningOnWindows() {
        return System.getProperty("os.name").toLowerCase().startsWith("windows");
    }
}

package com.github.nodevops.confd.maven.plugin;

/**
 * Created by pseillier on 28/12/2015.
 */
public class AbstractTest {
    private final static String BASEDIR;
    static {
        String basedir=System.getProperty("basedir");
        BASEDIR=basedir!=null?basedir:System.getProperty("user.dir");
    }
    protected String getBaseDir() {
        return BASEDIR;
    }
}

package com.github.nodevops.confd.maven.plugin;

import java.io.File;
import java.io.IOException;

import org.codehaus.plexus.util.FileUtils;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

public class AbstractTest {
    private final static String BASEDIR;
    protected static final String ENCODING = "UTF-8";

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder(new File(getBaseDir(), "target"));


    static {
        String basedir = System.getProperty("basedir");
        BASEDIR = basedir != null ? basedir : System.getProperty("user.dir");
    }

    protected String getBaseDir() {
        return BASEDIR;
    }

    protected File createTestFile(String[] lines) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (String s : lines) {
            sb.append(s).append('\n');
        }
        File f = temporaryFolder.newFile("test.dict");
        FileUtils.fileWrite(f, ENCODING, sb.toString());
        return f;
    }
}

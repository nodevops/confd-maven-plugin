package com.github.nodevops.confd.maven.plugin;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;

public class TmpTest {

    @Test
    @Ignore
    public void testPath() {
        File toto = new File("\\data\\ws-cc-config\\application.yml");

        System.out.println("isAbsolute?: "+toto.isAbsolute());
        System.out.println("absolutePath: "+toto.getAbsolutePath());
        System.out.println("path: "+toto.getPath());
    }
}


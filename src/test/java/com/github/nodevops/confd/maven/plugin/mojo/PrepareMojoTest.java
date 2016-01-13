package com.github.nodevops.confd.maven.plugin.mojo;

import com.github.nodevops.confd.maven.plugin.AbstractTest;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by pseillier on 21/12/2015.
 */

public class PrepareMojoTest extends AbstractTest {

    @Rule
    public MojoRule rule = new MojoRule();

    private File basedir;
    private File workDirectory;


    @Before
    public void setUp() throws Exception {

        basedir = new File(getBaseDir());
        workDirectory = new File(basedir, "target/prepare-mojo-confd");
    }

    @Test
    public void testPluginConfiguration() throws Exception {

        File pomFile = new File(basedir, "src/test/resources/unit/prepare-test/pom.xml");
        assertThat(pomFile).isNotNull();
        assertThat(pomFile).exists();

        PrepareMojo mojo = (PrepareMojo) rule.lookupMojo("prepare", pomFile);
        assertThat(mojo).isNotNull();
        rule.setVariableValueToObject(mojo, "basedir", basedir);
        rule.setVariableValueToObject(mojo, "workingDirectory", workDirectory);
        mojo.execute();
    }


}

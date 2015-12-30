package com.github.nodevops.confd.maven.plugin;

import com.github.nodevops.confd.maven.plugin.mojo.ProcessMojo;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by pseillier on 21/12/2015.
 */

public class ProcessorMojoTest extends AbstractTest {

    @Rule
    public MojoRule rule = new MojoRule();

    private File basedir;
    private File workDirectory;


    @Before
    public void setUp() throws Exception {

        basedir = new File(getBaseDir());
        workDirectory = new File(basedir, "target/confd");
    }

    @Test
    public void testPluginConfiguration() throws Exception {

        File pomFile = new File(basedir, "src/test/resources/unit/process-test/pom.xml");
        assertThat(pomFile).isNotNull();
        assertThat(pomFile).exists();

        ProcessMojo mojo = (ProcessMojo) rule.lookupMojo("process", pomFile);
        assertThat(mojo).isNotNull();
        rule.setVariableValueToObject(mojo, "basedir", basedir);
        rule.setVariableValueToObject(mojo, "workingDirectory", workDirectory);
        rule.setVariableValueToObject(mojo, "encoding", "UTF-8");
        mojo.execute();
    }


}

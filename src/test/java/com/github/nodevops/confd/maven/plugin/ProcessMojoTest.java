package com.github.nodevops.confd.maven.plugin;

import com.github.nodevops.confd.maven.plugin.mojo.ProcessMojo;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.testing.MojoRule;
import org.codehaus.plexus.interpolation.os.OperatingSystemUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assume.assumeTrue;

/**
 * Created by pseillier on 21/12/2015.
 */

public class ProcessMojoTest extends AbstractTest {

    @Rule
    public MojoRule rule = new MojoRule();

    private File basedir;
    private File workDirectory;


    @Before
    public void setUp() throws Exception {
        basedir = new File(getBaseDir());
        workDirectory = new File(basedir, "target/process-mojo-confd");
        FileUtils.copyDirectory(new File(basedir, "target/test-classes/unit/process-test/files"), workDirectory);
    }

    @Test
    public void testMojoExecution() throws Exception {
        assumeTrue(!TestUtils.isRunningOnWindows());
        System.out.println("Running this test because I'm not running on Windows");
        File pomFile = new File(basedir, "target/test-classes/unit/process-test/pom.xml");
        assertThat(pomFile).isNotNull();
        assertThat(pomFile).exists();

        ProcessMojo processMojo = (ProcessMojo) rule.lookupMojo("process", pomFile);
        assertThat(processMojo).isNotNull();
        rule.setVariableValueToObject(processMojo, "basedir", basedir);
        rule.setVariableValueToObject(processMojo, "workingDirectory", workDirectory);
        rule.setVariableValueToObject(processMojo, "encoding", "UTF-8");
        processMojo.execute();

        assertThat(new File(basedir,"/target/process-mojo-confd/file01.properties")).exists().isFile();
        assertThat(new File(basedir,"/target/process-mojo-confd/datasource.xml")).exists().isFile();
    }


}

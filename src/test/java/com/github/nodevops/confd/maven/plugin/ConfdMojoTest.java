package com.github.nodevops.confd.maven.plugin;

import com.github.nodevops.confd.maven.plugin.mojo.ConfdMojo;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by pseillier on 21/12/2015.
 */

public class ConfdMojoTest extends AbstractMojoTestCase{

   private File basedir;
   private File workDirectory;

   @Override
   protected void setUp() throws Exception {
      super.setUp();
      basedir=new File(getBasedir());
      workDirectory=new File(basedir,"target/confd");
   }

   public void testPluginConfiguration() throws Exception {
     File pomFile = getTestFile("src/test/resources/unit/project-to-test/pom.xml");
      assertThat(pomFile).isNotNull();
      assertThat(pomFile).exists();

      ConfdMojo mojo=(ConfdMojo)lookupMojo("process-templates",pomFile);
      setVariableValueToObject(mojo,"basedir",basedir);
      setVariableValueToObject(mojo,"workingDirectory",workDirectory);
      setVariableValueToObject(mojo,"encoding","UTF8");
      mojo.execute();
   }
}

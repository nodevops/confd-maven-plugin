package com.github.nodevops.confd.maven.plugin.processors.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.codehaus.plexus.util.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.github.nodevops.confd.maven.plugin.AbstractTest;
import com.github.nodevops.confd.maven.plugin.model.TemplateConfig;
import com.github.nodevops.confd.maven.plugin.processors.ProcessorContext;
import com.github.nodevops.confd.maven.plugin.processors.ProcessorContextBuilder;
import com.github.nodevops.confd.maven.plugin.utils.WorkingDirectoryUtil;

public class JavaProcessorImplTest extends AbstractTest {
    private static final String ENCODING = "UTF-8";

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder(new File(getBaseDir(), "target"));;

    private File resourcesDir;
    private File testDir;
    private File dictionaryFile;

    @Before
    public void setUp() throws Exception {
        resourcesDir = new File(getBaseDir(), "src/test/resources/unit/java-processor-test");
        testDir = temporaryFolder.newFolder();
        dictionaryFile = new File(testDir, "dictionaries/env01.dict");

        FileUtils.mkdir(new File(testDir, "conf.d").getAbsolutePath());
        FileUtils.copyDirectoryStructure(resourcesDir, testDir);

    }

    @Test
    public void shouldSuccessWithTemplateAsProperties() throws Exception {

        File tomlFile = new File(testDir, "conf.d/template01.toml");
        File destinationFile = new File(testDir, "file01.properties");
        File expetedFile = new File(resourcesDir, "expected01.properties");

        TemplateConfig templateConfig = new TemplateConfig();
        templateConfig.setSrc(new File("template01.tmpl"));
        templateConfig.setDest(destinationFile);
        templateConfig.setKeys(new String[] { "/web" });

        WorkingDirectoryUtil.writeToml(tomlFile, templateConfig);

        ProcessorContext context = new ProcessorContextBuilder()
                .workingDirectory(testDir)
                .dictionaryPath(dictionaryFile)
                .encoding(ENCODING)
                .build();

        JavaProcessorImpl javaProcessor = new JavaProcessorImpl();
        javaProcessor.process(context);

        assertThat(destinationFile).exists();
        assertThat(destinationFile).hasSameContentAs(expetedFile);
    }

    @Test
    public void shouldSuccessWithTemplateAsXML() throws Exception {

        File tomlFile = new File(testDir, "conf.d/template02.toml");
        File destinationFile = new File(testDir, "file02.xml");
        File expetedFile = new File(resourcesDir, "expected02.xml");

        TemplateConfig templateConfig = new TemplateConfig();
        templateConfig.setSrc(new File("template02.tmpl"));
        templateConfig.setDest(destinationFile);
        templateConfig.setKeys(new String[] { "/web" });

        WorkingDirectoryUtil.writeToml(tomlFile, templateConfig);

        ProcessorContext context = new ProcessorContextBuilder()
                .workingDirectory(testDir)
                .dictionaryPath(dictionaryFile)
                .encoding(ENCODING)
                .build();

        JavaProcessorImpl javaProcessor = new JavaProcessorImpl();
        javaProcessor.process(context);

        assertThat(destinationFile).exists();
        assertThat(destinationFile).hasSameContentAs(expetedFile);
    }

}

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
import com.github.nodevops.confd.maven.plugin.utils.WorkingDirectoryUtil;

public class JavaProcessorImplTest extends AbstractTest {
    private static final String ENCODING = "UTF-8";

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder(new File(getBaseDir(), "target"));
    ;

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
    public void shouldSuccessWithTemplateAsPropertiesAndTargetInANonExistingDirectory() throws Exception {

        File tomlFile = new File(testDir, "conf.d/template01.toml");
        File destinationFile = new File(testDir, "subDirectory/file01.properties");
        File expectedFile = new File(resourcesDir, "expected01.properties");

        TemplateConfig templateConfig = new TemplateConfig();
        templateConfig.setSrc(new File("template01.tmpl"));
        templateConfig.setDest(destinationFile);
        templateConfig.setKeys(new String[]{"/web"});

        WorkingDirectoryUtil.writeToml(tomlFile, templateConfig);

        ProcessorContext context = ProcessorContext.builder()
            .workingDirectory(testDir)
            .dictionaryPath(dictionaryFile)
            .encoding(ENCODING)
            .mkdirs(true)
            .build();

        JavaProcessorImpl javaProcessor = new JavaProcessorImpl();
        javaProcessor.process(context);

        assertThat(destinationFile).exists();
        assertThat(destinationFile).hasSameContentAs(expectedFile);
    }

    @Test
    public void shouldSuccessWithTemplateAsXML() throws Exception {

        File tomlFile = new File(testDir, "conf.d/template02.toml");
        File destinationFile = new File(testDir, "file02.xml");
        File expectedFile = new File(resourcesDir, "expected02.xml");

        TemplateConfig templateConfig = new TemplateConfig();
        templateConfig.setSrc(new File("template02.tmpl"));
        templateConfig.setDest(destinationFile);
        templateConfig.setKeys(new String[]{"/web"});

        WorkingDirectoryUtil.writeToml(tomlFile, templateConfig);

        ProcessorContext context = ProcessorContext.builder()
            .workingDirectory(testDir)
            .dictionaryPath(dictionaryFile)
            .encoding(ENCODING)
            .build();

        JavaProcessorImpl javaProcessor = new JavaProcessorImpl();
        javaProcessor.process(context);

        assertThat(destinationFile).exists();
        assertThat(destinationFile).hasSameContentAs(expectedFile);
    }

}

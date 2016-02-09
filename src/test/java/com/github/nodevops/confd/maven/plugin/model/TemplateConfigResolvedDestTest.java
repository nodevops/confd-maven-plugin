package com.github.nodevops.confd.maven.plugin.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class TemplateConfigResolvedDestTest {
    public static final String AN_ABSOLUTE_PATH = "/an/absolute/path";
    private final TemplateConfig templateConfig;
    private final String expected;


    public TemplateConfigResolvedDestTest(TemplateConfig templateConfig, String expected) {
        this.templateConfig = templateConfig;
        this.expected = expected;
    }

    @Test
    public void testGetResolvedDestPath() {
        assertThat(templateConfig.getResolvedDestPath()).isEqualTo(expected);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {

        TemplateConfig c1 = new TemplateConfig();
        c1.setDest(AN_ABSOLUTE_PATH);

        TemplateConfig c2 = new TemplateConfig();
        c2.setDest(AN_ABSOLUTE_PATH);
        c2.setForceDestToLocalFileSystemType(false);

        List<Object[]> params = new ArrayList<Object[]>(
            Arrays.asList(new Object[][]{
                {c1, AN_ABSOLUTE_PATH},
                {c2, AN_ABSOLUTE_PATH},
            }));

        System.out.println("Running testGetResolvedDestPath on a [" + System.getProperty("os.name") + "] system");
        if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
            TemplateConfig c3 = new TemplateConfig();
            c3.setDest(AN_ABSOLUTE_PATH);
            c3.setForceDestToLocalFileSystemType(true);
            params.add(new Object[]{c3, "\\an\\absolute\\path"});

            TemplateConfig c4 = new TemplateConfig();
            c4.setDest("C:\\a\\windows\\absolute\\path");
            c4.setForceDestToLocalFileSystemType(true);
            params.add(new Object[]{c4, "C:\\a\\windows\\absolute\\path"});
        }

        return params;
    }
}

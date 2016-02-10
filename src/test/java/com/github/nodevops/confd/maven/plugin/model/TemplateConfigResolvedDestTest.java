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
    private final boolean forceDestToLocalFileSystemType;


    public TemplateConfigResolvedDestTest(TemplateConfig templateConfig, String expected, boolean forceDestToLocalFileSystemType) {
        this.templateConfig = templateConfig;
        this.expected = expected;
        this.forceDestToLocalFileSystemType = forceDestToLocalFileSystemType;
    }

    @Test
    public void testGetResolvedDestPath() {
        assertThat(templateConfig.getResolvedDestPath(forceDestToLocalFileSystemType)).isEqualTo(expected);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {

        TemplateConfig c1 = new TemplateConfig();
        c1.setDest(AN_ABSOLUTE_PATH);

        List<Object[]> params = new ArrayList<Object[]>(
            Arrays.asList(new Object[][]{
                {c1, AN_ABSOLUTE_PATH, false},
            }));

        System.out.println("Running testGetResolvedDestPath on a [" + System.getProperty("os.name") + "] system");
        if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
            TemplateConfig c3 = new TemplateConfig();
            c3.setDest(AN_ABSOLUTE_PATH);
            params.add(new Object[]{c3, "\\an\\absolute\\path", true});

            TemplateConfig c4 = new TemplateConfig();
            c4.setDest("C:\\a\\windows\\absolute\\path");
            params.add(new Object[]{c4, "C:\\a\\windows\\absolute\\path", true});
        }

        return params;
    }
}

package com.github.nodevops.confd.maven.plugin.utils;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.codehaus.plexus.util.FileUtils;

import com.github.nodevops.confd.maven.plugin.model.TemplateConfig;

public class WorkingDirectoryUtil {
    private static final char QUOTE = '\'';
    private static final char LINE_SEPARATOR = '\n';

    public static void generateConfdArtefacts(File workingDirectory, List<TemplateConfig> templates) throws IOException {

        File templatesDirectory = new File(workingDirectory, "templates");
        File tomlDirectory = new File(workingDirectory, "conf.d");

        if (workingDirectory.exists()) {
            FileUtils.deleteDirectory(workingDirectory);
        }

        FileUtils.mkdir(templatesDirectory.getAbsolutePath());
        FileUtils.mkdir(tomlDirectory.getAbsolutePath());
        for (TemplateConfig tc : templates) {
            String tomlBaseName = FileUtils.basename(tc.getSrc().getAbsolutePath()) + "toml";
            File tomlFile = new File(tomlDirectory, tomlBaseName);
            writeToml(tomlFile, tc);

            FileUtils.copyFileToDirectory(tc.getSrc(), templatesDirectory);

        }
    }

    public static void writeToml(File tomlFile, TemplateConfig tc) throws IOException {
        StringWriter sw = new StringWriter();
        sw.append("[template]")
            .append(LINE_SEPARATOR);

        sw.append("src = ")
            .append(QUOTE)
            .append(FileUtils.removePath(tc.getSrc().getPath()))
            .append(QUOTE)
            .append(LINE_SEPARATOR);

        sw.append("dest = ")
            .append(QUOTE)
            .append(tc.getDest().getAbsolutePath())
            .append(QUOTE)
            .append(LINE_SEPARATOR);

        sw.append("keys = [")
            .append(LINE_SEPARATOR);
        for (String key : tc.getKeys()) {
            sw.append(QUOTE)
                .append(key)
                .append(QUOTE)
                .append(',')
                .append(LINE_SEPARATOR);
        }

        sw.append(']')
            .append(LINE_SEPARATOR);

        FileUtils.fileWrite(tomlFile, "UTF8", sw.toString());
    }

}

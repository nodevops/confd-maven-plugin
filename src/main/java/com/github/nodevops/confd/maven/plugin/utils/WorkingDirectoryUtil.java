package com.github.nodevops.confd.maven.plugin.utils;

import com.github.nodevops.confd.maven.plugin.model.TemplateConfig;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

/**
 * Created by pseillier on 22/12/2015.
 */
public class WorkingDirectoryUtil {

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


    private static void writeToml(File tomlFile, TemplateConfig tc) throws IOException {
        StringWriter sw = new StringWriter();
        sw.append("[template]").append('\n');
        sw.append("src = ").append('"').append(FileUtils.removePath(tc.getSrc().getPath())).append('"').append('\n');
        sw.append("dest = ").append('"').append(tc.getDest().getAbsolutePath()).append('"').append('\n');
        sw.append("keys = [").append('\n');
        for (String key : tc.getKeys()) {
            sw.append('"').append(key).append('"').append(',').append('\n');
        }
        sw.append(']').append('\n');
        FileUtils.fileWrite(tomlFile, "UTF8", sw.toString());
    }

}

package com.github.nodevops.confd.maven.plugin.utils;

import static com.github.nodevops.confd.maven.plugin.ConfdConsts.CONF_D_DIRECTORY;
import static com.github.nodevops.confd.maven.plugin.ConfdConsts.TEMPLATES_DIRECTORY;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.FileUtils;

import com.github.nodevops.confd.maven.plugin.model.TemplateConfig;
import com.github.nodevops.confd.maven.plugin.templating.Parser;
import com.google.common.collect.Maps;

public class WorkingDirectoryUtil {
    private static final char QUOTE = '\'';
    private static final char LINE_SEPARATOR = '\n';
    public static final String TOML_FILE_EXT = "toml";

    public static void generateConfdArtefacts(
        PrepareContext context,
        List<TemplateConfig> templates,
        boolean forceDestToLocalFileSystemType,
        Log log
    ) throws IOException {

        File workingDirectory = context.getWorkingDirectory();

        File templatesDirectory = new File(workingDirectory, TEMPLATES_DIRECTORY);
        File tomlDirectory = new File(workingDirectory, CONF_D_DIRECTORY);

        if (workingDirectory.exists()) {
            FileUtils.deleteDirectory(workingDirectory);
        }

        FileUtils.mkdir(templatesDirectory.getAbsolutePath());
        FileUtils.mkdir(tomlDirectory.getAbsolutePath());
        for (TemplateConfig tc : templates) {
            String tomlBaseName = FileUtils.basename(tc.getSrc().getAbsolutePath()) + TOML_FILE_EXT;
            File tomlFile = new File(tomlDirectory, tomlBaseName);
            writeToml(tomlFile, tc, forceDestToLocalFileSystemType);
            FileUtils.copyFileToDirectory(tc.getSrc(), templatesDirectory);
            warnAboutKeysExcludedByNamespace(tc, context, log);
        }
    }

    public static void warnAboutKeysExcludedByNamespace(TemplateConfig tc, PrepareContext context, Log log) {
        Parser parser = new Parser(tc.getSrc(), context.getEncoding(), Parser.ParserType.GATHER);
        try {
            parser.parse(Maps.<String, String>newHashMap());
            List<String> keys = parser.getTemplateKeys();
            List<String> offendingKeys = new NamespaceChecker(keys, tc.getKeys()).getUnmatchedKeys();
            if (!offendingKeys.isEmpty()) {
                log.error("=========================================================");
                log.error("Template definition : " + tc.getId());
                log.error("Template src : " + tc.getSrc());
                log.error("Something is wrong with the namespace (keys) definition," +
                    " some of the keys required in the templates are not included by the namespaces");
                log.error("Namespaces:");
                for (String ns : tc.getKeys()) {
                    log.error(ns);
                }
                log.error("Keys excluded by the namespaces:");
                for (String key : offendingKeys) {
                    log.error(key);
                }
                log.error("=========================================================");
            }
        } catch (IOException e) {
            log.error("Something is wrong with the template file <" + tc.getSrc() + ">");
            log.error(e);
        }
    }

    public static void writeToml(
        File tomlFile,
        TemplateConfig tc,
        boolean globalForceDestToLocalFileSystemType) throws IOException {
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
            .append(tc.getResolvedDestPath(globalForceDestToLocalFileSystemType))
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

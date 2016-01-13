package com.github.nodevops.confd.maven.plugin.processors.impl;

import com.github.nodevops.confd.maven.plugin.processors.Processor;
import com.github.nodevops.confd.maven.plugin.processors.ProcessorContext;
import com.github.nodevops.confd.maven.plugin.processors.ProcessorExecutionException;
import com.github.nodevops.confd.maven.plugin.templating.Parser;
import com.github.nodevops.confd.maven.plugin.utils.DictionaryException;
import com.github.nodevops.confd.maven.plugin.utils.DictionaryUtil;
import com.moandjiezana.toml.Toml;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by pseillier on 22/12/2015.
 */
public class JavaProcessorImpl implements Processor {

    @Override
    public void process(ProcessorContext context) throws ProcessorExecutionException {
        // the toml files are in the conf.d sub directory
        File tomlDirectory = new File(context.getWorkingDirectory(), "conf.d");
        File templatesDirectory = new File(context.getWorkingDirectory(), "templates");
        try {
            // load dictionary
            Map<String, String> env = DictionaryUtil.readDictionaryAsProperties(context.getDictionaryPath(), context.getEncoding());
            // browse the .toml files
            List<File> tomlFiles = FileUtils.getFiles(tomlDirectory, "*.toml", null);
            for (File tomlFile : tomlFiles) {
                // process each toml file
                try {
                    processToml(tomlFile, templatesDirectory, env, context.getEncoding());
                } catch (IOException e) {
                    throw new ProcessorExecutionException("unable to process toml file  " + tomlFile, e);
                }
            }
        } catch (DictionaryException e) {
            throw new ProcessorExecutionException("unable to load dictionary " + context.getDictionaryPath(), e);
        } catch (IllegalStateException e) {
            throw new ProcessorExecutionException("unable to process toml files from directory " + tomlDirectory, e);
        } catch (IOException e) {
            throw new ProcessorExecutionException("unable to process toml files from directory " + tomlDirectory, e);
        }
    }

    private void processToml(File tomlFile, File templatesDirectory, Map<String, String> env, String encoding) throws IOException {
        Toml toml = new Toml().read(tomlFile);
        String src = toml.getString("template.src");
        String dest = toml.getString("template.dest");
        List<String> keys = toml.getList("template.keys");

        File templateFile = new File(templatesDirectory, src);
        File destFile = new File(dest);


        Parser parser = new Parser(templateFile, encoding);

        String parsedTemplate = parser.parse(env);
        FileUtils.fileWrite(destFile, encoding, parsedTemplate);
    }
}

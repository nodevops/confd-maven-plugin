package com.github.nodevops.confd.maven.plugin.processors.impl;

import static com.github.nodevops.confd.maven.plugin.ConfdConsts.CONF_D_DIRECTORY;
import static com.github.nodevops.confd.maven.plugin.ConfdConsts.TEMPLATES_DIRECTORY;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.plexus.util.FileUtils;

import com.github.nodevops.confd.maven.plugin.processors.Processor;
import com.github.nodevops.confd.maven.plugin.processors.ProcessorContext;
import com.github.nodevops.confd.maven.plugin.processors.ProcessorExecutionException;
import com.github.nodevops.confd.maven.plugin.templating.Parser;
import com.github.nodevops.confd.maven.plugin.utils.DictionaryException;
import com.github.nodevops.confd.maven.plugin.utils.DictionaryUtil;
import com.google.common.collect.Sets;
import com.moandjiezana.toml.Toml;

public class JavaProcessorImpl implements Processor {


    @Override
    public void process(ProcessorContext context) throws ProcessorExecutionException {
        // the toml files are in the conf.d sub directory
        File tomlDirectory = new File(context.getWorkingDirectory(), CONF_D_DIRECTORY);
        File templatesDirectory = new File(context.getWorkingDirectory(), TEMPLATES_DIRECTORY);
        try {
            // load dictionary
            Map<String, String> env = DictionaryUtil.readDictionaryAsProperties(context.getDictionaryPath(), context.getEncoding());
            // browse the .toml files
            List<File> tomlFiles = FileUtils.getFiles(tomlDirectory, "*.toml", null);
            Set<String> allTemplateKeys = Sets.newHashSet();
            for (File tomlFile : tomlFiles) {
                // process each toml file
                try {
                    allTemplateKeys.addAll(
                        processToml(tomlFile, templatesDirectory, env, context.getEncoding(), context.isMkdirs()));
                } catch (IOException e) {
                    throw new ProcessorExecutionException("unable to process toml file  " + tomlFile, e);
                }
            }
            //TODO: compare used keys with dict keys
        } catch (DictionaryException e) {
            throw new ProcessorExecutionException("unable to load dictionary " + context.getDictionaryPath(), e);
        } catch (IllegalStateException e) {
            throw new ProcessorExecutionException("unable to process toml files from directory " + tomlDirectory, e);
        } catch (IOException e) {
            throw new ProcessorExecutionException("unable to process toml files from directory " + tomlDirectory, e);
        }
    }

    private List<String> processToml(
        File tomlFile,
        File templatesDirectory,
        Map<String, String> env,
        String encoding,
        boolean mkdirs
    ) throws IOException {
        Toml toml = new Toml().read(tomlFile);
        String src = toml.getString("template.src");
        String dest = toml.getString("template.dest");
        List<String> keys = toml.getList("template.keys");

        if (keys == null || keys.size() == 0) {
            throw new IOException("Something went wrong while processing the toml file <" + tomlFile +
                ">: the 'keys' section must exist and contain at least one key");
        }

        // filter the env map according to the keys defined in the toml
        HashMap<String, String> filteredEnv = new HashMap<String, String>();
        for (Map.Entry<String, String> entry : env.entrySet()) {
            for (String key : keys) {
                if (entry.getKey().startsWith(key)) {
                    filteredEnv.put(entry.getKey(), entry.getValue());
                }
            }
        }
        File templateFile = new File(templatesDirectory, src);
        File destFile = new File(dest);
        if (mkdirs) {
            destFile.getParentFile().mkdirs();
        }
        Parser parser = new Parser(templateFile, encoding);
        String parsedTemplate = parser.parse(filteredEnv);
        FileUtils.fileWrite(destFile, encoding, parsedTemplate);
        return parser.getTemplateKeys();
    }
}


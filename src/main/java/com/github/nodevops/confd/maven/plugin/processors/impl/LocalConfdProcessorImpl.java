package com.github.nodevops.confd.maven.plugin.processors.impl;

import static com.github.nodevops.confd.maven.plugin.ConfdConsts.CONF_D_DIRECTORY;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.codehaus.plexus.util.FileUtils;

import com.github.nodevops.confd.maven.plugin.processors.Processor;
import com.github.nodevops.confd.maven.plugin.processors.ProcessorContext;
import com.github.nodevops.confd.maven.plugin.processors.ProcessorExecutionException;
import com.github.nodevops.confd.maven.plugin.utils.DictionaryException;
import com.github.nodevops.confd.maven.plugin.utils.DictionaryUtil;
import com.moandjiezana.toml.Toml;

public class LocalConfdProcessorImpl implements Processor {

    private String confdBinaryPath;

    private Log log = new SystemStreamLog();

    public LocalConfdProcessorImpl(String confdBinaryPath) {
        this.confdBinaryPath = confdBinaryPath;
    }

    @Override
    public void process(ProcessorContext context) throws ProcessorExecutionException {
        ProcessBuilder processBuilder = new ProcessBuilder(
            confdBinaryPath,
            "-onetime",
            "-backend",
            "env",
            "-confdir",
            context.getWorkingDirectory().getAbsolutePath()
        );
        if (context.isMkdirs()) {
            File tomlDirectory = new File(context.getWorkingDirectory(), CONF_D_DIRECTORY);
            try {
                List<File> tomlFiles = FileUtils.getFiles(tomlDirectory, "*.toml", null);
                for (File tomlFile : tomlFiles) {
                    // process each toml file
                    createTargetDirIfNeeded(tomlFile, context.isMkdirs());
                }
            } catch (IOException ioe) {
                throw new ProcessorExecutionException("unable to process toml files in " + tomlDirectory, ioe);
            }
        }
        try {
            log.info("Start process " + processBuilder.command());
            processBuilder
                .environment()
                .putAll(
                    DictionaryUtil.readDictionaryAsEnvVariables(context.getDictionaryPath(), context.getEncoding()));
            Process process = processBuilder.start();
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                log.error(line);
            }
            process.waitFor();

        } catch (IOException e) {
            throw new ProcessorExecutionException("Unable to start process " + processBuilder, e);
        } catch (InterruptedException e) {
            throw new ProcessorExecutionException("The process " + processBuilder + " has been interrupted");
        } catch (DictionaryException e) {
            throw new ProcessorExecutionException("Unable to start process " + processBuilder, e);
        }
    }

    public void createTargetDirIfNeeded(File tomlFile, boolean mkdirs) {
        Toml toml = new Toml().read(tomlFile);
        String dest = toml.getString("template.dest");
        File destFile = new File(dest);
        if (mkdirs) {
            destFile.getParentFile().mkdirs();
        }
    }
}

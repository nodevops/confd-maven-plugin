package com.github.nodevops.confd.maven.plugin.processors.impl;

import static com.github.nodevops.confd.maven.plugin.ConfdConsts.CONF_D_DIRECTORY;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.LogOutputStream;
import org.apache.commons.exec.PumpStreamHandler;
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
            CommandLine confdCmdLine = new CommandLine(confdBinaryPath);
            confdCmdLine.addArgument("-onetime");
            confdCmdLine.addArgument("-backend");
            confdCmdLine.addArgument("env");
            confdCmdLine.addArgument("-confdir");
            confdCmdLine.addArgument(context.getWorkingDirectory().getAbsolutePath());

            DefaultExecutor executor = new DefaultExecutor();
            executor.setStreamHandler(new PumpStreamHandler(new LogOutputStream() {
                @Override
                protected void processLine(String line, int level) {
                    log.info(line);
                }
            }));
            log.info("Start process " + confdCmdLine);
            int exitCode = executor.execute(
                confdCmdLine,
                DictionaryUtil.readDictionaryAsEnvVariables(context.getDictionaryPath(), context.getEncoding()));

        } catch (IOException e) {
            throw new ProcessorExecutionException("Unable to start confd", e);
        } catch (DictionaryException e) {
            throw new ProcessorExecutionException("Unable to start confd", e);
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

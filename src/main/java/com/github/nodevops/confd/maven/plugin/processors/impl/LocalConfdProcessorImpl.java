package com.github.nodevops.confd.maven.plugin.processors.impl;

import com.github.nodevops.confd.maven.plugin.processors.Processor;
import com.github.nodevops.confd.maven.plugin.processors.ProcessorContext;
import com.github.nodevops.confd.maven.plugin.processors.ProcessorExecutionException;
import com.github.nodevops.confd.maven.plugin.utils.DictionaryException;
import com.github.nodevops.confd.maven.plugin.utils.DictionaryUtil;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by pseillier on 22/12/2015.
 */
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


        try {
            log.info("Start process " + processBuilder.command());
            processBuilder.environment().putAll(DictionaryUtil.readDictionaryAsEnvVariables(context.getDictionaryPath(), context.getEncoding()));
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
}

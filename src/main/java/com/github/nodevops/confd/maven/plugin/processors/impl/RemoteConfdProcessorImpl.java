package com.github.nodevops.confd.maven.plugin.processors.impl;

import java.net.URL;

import com.github.nodevops.confd.maven.plugin.processors.Processor;
import com.github.nodevops.confd.maven.plugin.processors.ProcessorContext;
import com.github.nodevops.confd.maven.plugin.processors.ProcessorExecutionException;

public class RemoteConfdProcessorImpl implements Processor {
    private URL httpURL;

    public RemoteConfdProcessorImpl(URL httpURL) {

        this.httpURL = httpURL;
    }

    @Override
    public void process(ProcessorContext context) throws ProcessorExecutionException {

    }
}

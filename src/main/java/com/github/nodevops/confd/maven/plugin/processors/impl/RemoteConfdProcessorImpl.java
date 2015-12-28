package com.github.nodevops.confd.maven.plugin.processors.impl;

import com.github.nodevops.confd.maven.plugin.processors.Processor;
import com.github.nodevops.confd.maven.plugin.processors.ProcessorContext;
import com.github.nodevops.confd.maven.plugin.processors.ProcessorExcecutionException;

import java.net.URL;

/**
 * Created by pseillier on 22/12/2015.
 */
public class RemoteConfdProcessorImpl implements Processor {
   private URL httpURL;

   public RemoteConfdProcessorImpl(URL httpURL) {

      this.httpURL = httpURL;
   }

   @Override
   public void process(ProcessorContext context) throws ProcessorExcecutionException {

   }
}

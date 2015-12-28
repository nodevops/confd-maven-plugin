package com.github.nodevops.confd.maven.plugin.processors;

/**
 * Created by pseillier on 22/12/2015.
 */
public interface Processor {
   public void process(ProcessorContext context) throws ProcessorExcecutionException;
}

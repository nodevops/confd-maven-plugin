package com.github.nodevops.confd.maven.plugin.processors;

import com.github.nodevops.confd.maven.plugin.model.TemplateConfig;

import java.io.File;
import java.util.List;

/**
 * Created by pseillier on 28/12/2015.
 */
public class ProcessorContextBuilder {
   private ProcessorContext processorContext;

   public ProcessorContextBuilder() {
      processorContext=new ProcessorContext();
   }

   public ProcessorContextBuilder templates(List<TemplateConfig> templates) {
      processorContext.setTemplates(templates);
      return this;
   }

   public ProcessorContextBuilder dictionaryPath(File dictionaryPath) {
      processorContext.setDictionaryPath(dictionaryPath);
      return this;
   }

   public ProcessorContextBuilder workingDirectory(File workingDirectory) {
      processorContext.setWorkingDirectory(workingDirectory);
      return this;
   }


   public ProcessorContextBuilder encoding(String encoding) {
      processorContext.setEncoding(encoding);
      return this;
   }

   public ProcessorContext build() {
      return processorContext;
   }

}

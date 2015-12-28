package com.github.nodevops.confd.maven.plugin.processors;

import com.github.nodevops.confd.maven.plugin.model.TemplateConfig;

import java.io.File;
import java.util.List;

/**
 * Created by pseillier on 28/12/2015.
 */
public class ProcessorContext {
   private List<TemplateConfig> templates;
   private File dictionaryPath;
   private File workingDirectory;
   private String encoding;

   /* package */ ProcessorContext() {

   }
   public List<TemplateConfig> getTemplates() {
      return templates;
   }

   public void setTemplates(List<TemplateConfig> templates) {
      this.templates = templates;
   }

   public File getDictionaryPath() {
      return dictionaryPath;
   }

   public void setDictionaryPath(File dictionaryPath) {
      this.dictionaryPath = dictionaryPath;
   }

   public File getWorkingDirectory() {
      return workingDirectory;
   }

   public void setWorkingDirectory(File workingDirectory) {
      this.workingDirectory = workingDirectory;
   }

   public void setEncoding(String encoding) {
      this.encoding = encoding;
   }

   public String getEncoding() {
      return encoding;
   }
}

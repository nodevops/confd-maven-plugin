package com.github.nodevops.confd.maven.plugin.model;

import java.util.Properties;

/**
 * Created by pseillier on 21/12/2015.
 */
public class ProcessorConfig {

   private String name;
   private Properties properties;

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public Properties getProperties() {
      return properties;
   }

   public void setProperties(Properties properties) {
      this.properties = properties;
   }

   @Override
   public String toString() {
      return "ProcessorConfig{" +
         "name='" + name + '\'' +
         ", properties=" + properties +
         '}';
   }
}

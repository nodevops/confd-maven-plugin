package com.github.nodevops.confd.maven.plugin.processors;

/**
 * Created by pseillier on 22/12/2015.
 */
public class ProcessorCreationException extends Exception{
   public ProcessorCreationException(String message) {
      super(message);
   }

   public ProcessorCreationException(String message, Throwable cause) {
      super(message, cause);
   }
}

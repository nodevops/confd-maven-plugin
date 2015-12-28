package com.github.nodevops.confd.maven.plugin.processors;

/**
 * Created by pseillier on 22/12/2015.
 */
public class ProcessorExcecutionException extends Exception{
   public ProcessorExcecutionException(String message) {
      super(message);
   }

   public ProcessorExcecutionException(String message, Throwable cause) {
      super(message, cause);
   }
}

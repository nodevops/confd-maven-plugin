package com.github.nodevops.confd.maven.plugin.utils;

import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pseillier on 28/12/2015.
 */
public class DictionaryUtil {
   public static Map<String,String> readDictionaryAsEnvVariables(File dictionary, String encoding) throws IOException {
      List<String> lines = FileUtils.loadFile(dictionary);
      Map<String,String> env = new HashMap<String, String>();
      for(String line:lines) {
         String[] split= StringUtils.split(line,"=",2);
         if(split.length<2) {
            throw new IOException("dictionary " + dictionary +" : bad key/value format for "+line);
         }
         env.put(normalize(split[0]),split[1]);

      }
      return env;
   }

   private static String normalize(String src) throws IOException{
      if(!src.startsWith("/")) {
         throw new IOException("key " + src + " must start with a '/' ");
      }
      return StringUtils.stripStart(src,"/").toUpperCase().replace('/','_');

   }

}

package com.github.nodevops.confd.maven.plugin.utils;

import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by pseillier on 28/12/2015.
 */
public class DictionaryUtil {
    public static Map<String, String> readDictionaryAsEnvVariables(File dictionary, String encoding) throws DictionaryException {

        try {
            List<String> lines = FileUtils.loadFile(dictionary);
            // the file must contain at least one key/value pair
            if (lines.isEmpty()) {
                throw new DictionaryException("dictionary " + dictionary + " does not contain key/value pair");
            }
            Map<String, String> env = new HashMap<String, String>();
            for (String line : lines) {
                String[] split = StringUtils.split(line, "=", 2);
                if (split.length < 2) {
                    throw new DictionaryException("dictionary " + dictionary + " : bad key/value format for " + line);
                }
                env.put(normalizeKey(split[0]), split[1]);

            }
            return env;
        } catch (IOException e) {
            throw new DictionaryException("unable to load " + dictionary, e);
        } catch (InvalidKeyFormatException e) {
            throw new DictionaryException("unable to load " + dictionary, e);
        }

    }

    /**
     * Convert a key from confd format "/part1/part2/...." to  shell variable format "PART1_PART2_....".
     * The valid format for the input key is :
     * <code>(/[a-zA-Z0-9]+)+</code>
     *
     * @param key the key to convert
     * @return a new key in shell variable format
     * @throws DictionaryException - If the key format is not valid
     */
    private static String normalizeKey(String key) throws InvalidKeyFormatException {

        // check the key format
        if (!Pattern.matches("(/[A-Za-z0-9]+)+", key)) {
            throw new InvalidKeyFormatException("the key " + key + " format is not valid. Valid format is (/[a-zA-Z0-9]+)+");
        }
        // convert confd key format /part1/part2/.../... to shell variable PART1_PART2_..._...
        String upperCaseKey = StringUtils.stripStart(key, "/").toUpperCase();

        return StringUtils.stripStart(key, "/").toUpperCase().replace('/', '_');

    }

}

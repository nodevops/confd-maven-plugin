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
 * Contains some helper methods to handle dictionaries. A dictionary is a file containing key/value pairs.
 * The file may contain comments which begin with a '#'.
 * The key/value separator is the '=' character.
 * The key format is :
 * - a sequence of parts
 * - a part begin with a "/" character followed by one or more uppercase letter
 * and/or lowercase letter and/or digit and or underscore character "_"
 * <p>
 * Example of valid key/value pairs :
 * <p>
 * <pre>
 *
 * /web/media_server/address = media.server.com
 * /web/home_page = http://www.home.com/
 * /proxy/http = server.proxy.int
 * /proxy/https = server.proxy.int
 * /users/user01 = user1
 * /user/use02 = user2
 *
 * </pre>
 */
public class DictionaryUtil {
    /**
     * Load the <code>dictionary</code> file into a <code>Map</code> according to the <code>encoding</code>.
     * <p>
     * The key names are converted to shell environment variable format.
     * For instance /part1/part2 is converted to PART1_PART2 (the first '/' is removed).
     *
     * @param dictionary the file path to load
     * @param encoding   the encoding of the file to load
     * @return a <code>Map</code> representation of the file
     * @throws DictionaryException if the file cannot be load or if a key/value is not valid.
     */
    public static Map<String, String> readDictionaryAsEnvVariables(File dictionary, String encoding) throws DictionaryException {

        try {
            List<String> lines = FileUtils.loadFile(dictionary);
            // the file must contain at least one key/value pair
            if (lines.isEmpty()) {
                throw new DictionaryException("dictionary " + dictionary + " does not contain key/value pair");
            }
            Map<String, String> env = new HashMap<String, String>();
            for (String line : lines) {
                String[] split = line.split("\\s*=\\s*", 2);

                if (split.length != 2) {
                    throw new DictionaryException("dictionary " + dictionary + " : bad key/value format for " + line);
                }
                if (StringUtils.isEmpty(split[1])) {
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
     * <code>(/\w+)+</code>
     *
     * @param key the key to convert
     * @return a new key in shell variable format
     * @throws DictionaryException - If the key format is not valid
     */
    private static String normalizeKey(String key) throws InvalidKeyFormatException {

        // check the key format
        if (!Pattern.matches("(/\\w+)+", key)) {
            throw new InvalidKeyFormatException("the key " + key + " format is not valid. Valid format is (/[a-zA-Z0-9]+)+");
        }
        // convert confd key format /part1/part2/.../... to shell variable PART1_PART2_..._...
        String upperCaseKey = StringUtils.stripStart(key, "/").toUpperCase();

        return StringUtils.stripStart(key, "/").toUpperCase().replace('/', '_');

    }

}

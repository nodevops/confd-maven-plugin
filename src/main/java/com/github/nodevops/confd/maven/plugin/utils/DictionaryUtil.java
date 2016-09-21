package com.github.nodevops.confd.maven.plugin.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.codehaus.plexus.util.StringUtils;

import com.google.common.collect.Maps;

/**
 * Contains some helper methods to handle dictionaries. A dictionary is a file containing key/value pairs. The file may contain comments
 * which begin with a '#'. The key/value separator is the '=' character. The key format is : - a sequence of parts - a part begin with a "/"
 * character followed by one or more uppercase letter and/or lowercase letter and/or digit and or underscore character "_"
 * <p>
 * Example of valid key/value pairs :
 * <p>
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
    public static final String DOES_NOT_CONTAIN_KEY_VALUE_PAIR_MESSAGE = "does not contain key/value pair";
    public static final String BAD_KEY_VALUE_FORMAT_FOR_LINE_MESSAGE = "bad key/value format for line";
    public static final String EMPTY_VALUES_ARE_NOT_ALLOWED_MESSAGE = "empty values are not allowed";

    /**
     * Load the <code>dictionary</code> file into a <code>Map</code> according to the <code>encoding</code>.
     * <p>
     * The key names are converted to shell environment variable format. For instance /part1/part2 is converted to PART1_PART2 (the first
     * '/' is removed).
     *
     * @param dictionary the file path to load
     * @param encoding   the encoding of the file to load
     * @return a <code>Map</code> representation of the file
     * @throws DictionaryException if the file cannot be load or if a key/value is not valid.
     */
    public static Map<String, String> readDictionaryAsEnvVariables(File dictionary, String encoding) throws DictionaryException {
        return readDictionary(dictionary, encoding, true);
    }

    /**
     * Load the <code>dictionary</code> file into a <code>Map</code> according to the <code>encoding</code>.
     * <p>
     * The key names format is unchanged
     *
     * @param dictionary the file path to load
     * @param encoding   the encoding of the file to load
     * @return a <code>Map</code> representation of the file
     * @throws DictionaryException if the file cannot be load or if a key/value is not valid.
     */
    public static Map<String, String> readDictionaryAsProperties(File dictionary, String encoding) throws DictionaryException {
        return readDictionary(dictionary, encoding, false);
    }

    private static Map<String, String> readDictionary(File dictionary, String encoding, boolean convertToEnv) throws DictionaryException {
        try {
            List<String> lines = FileUtils.readLines(dictionary, encoding);
            Map<String, String> env = Maps.newHashMap();
            int lineIndex = 0;
            for (String line : lines) {
                lineIndex++;
                if (!(line.isEmpty() || line.startsWith("#"))) {
                    if (!line.contains("=")) {
                        throw new DictionaryException(
                            "dictionary <" +
                            dictionary +
                            ":" + lineIndex +
                            "> " +
                            BAD_KEY_VALUE_FORMAT_FOR_LINE_MESSAGE +
                            " <" +
                            line +
                            ">");
                    }
                    String[] split = line.split("\\s*=\\s*", 2);

                    if (split.length != 2) {
                        throw new DictionaryException(
                            "dictionary <" +
                            dictionary +
                            ":" +
                            lineIndex +
                            "> " +
                            BAD_KEY_VALUE_FORMAT_FOR_LINE_MESSAGE +
                            " <" +
                            line +
                            ">");
                    }
                    if (StringUtils.isEmpty(split[1])) {
                        throw new DictionaryException(
                            "dictionary <" +
                            dictionary +
                            ":" +
                            lineIndex +
                            "> : " +
                            EMPTY_VALUES_ARE_NOT_ALLOWED_MESSAGE +
                            " <" +
                            line +
                            ">");
                    }
                    env.put(normalizeKey(split[0].trim(), convertToEnv), split[1].trim());
                }
            }
            // the file must contain at least one key/value pair
            if (env.isEmpty()) {
                throw new DictionaryException(
                    "dictionary <" +
                        dictionary +
                        "> " +
                        DOES_NOT_CONTAIN_KEY_VALUE_PAIR_MESSAGE);
            }
            return env;
        } catch (IOException e) {
            throw new DictionaryException(
                "unable to load file <" +
                    dictionary +
                    "> because of IOException with message <" +
                    e.getMessage() + ">"
                , e);
        } catch (InvalidKeyFormatException e) {
            throw new DictionaryException(
                "unable to load file <" +
                    dictionary +
                    "> because of an InvalidKeyFormatException with message <" +
                    e.getMessage() + ">"
                , e);
        }
    }

    /**
     * Convert a key from confd format "/part1/part2/...." to shell variable format "PART1_PART2_...." if the <code>convertToEnv</code> is
     * <code>true</code>, otherwise the key remains unchanged The valid format for the input key is : <code>(/\w+)+</code>
     *
     * @param key          the key to convert
     * @param convertToEnv true must convert the key name to to shell variable format
     * @return a new key in shell variable format
     * @throws DictionaryException - If the key format is not valid
     */
    private static String normalizeKey(String key, boolean convertToEnv) throws InvalidKeyFormatException {
        String result;
        // check the key format
        if (!Pattern.matches("(/\\w+)+", key)) {
            throw new InvalidKeyFormatException("the key " + key + " format is not valid. Valid format is (/[a-zA-Z0-9]+)+");
        }
        if (convertToEnv) {
            // convert confd key format /part1/part2/.../... to shell variable PART1_PART2_..._...
            result = StringUtils.stripStart(key, "/").toUpperCase().replace('/', '_');
        } else {
            result = key;
        }
        return result;
    }

}

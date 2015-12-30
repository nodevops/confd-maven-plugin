package com.github.nodevops.confd.maven.plugin;

import com.github.nodevops.confd.maven.plugin.utils.DictionaryException;
import com.github.nodevops.confd.maven.plugin.utils.DictionaryUtil;
import com.github.nodevops.confd.maven.plugin.utils.InvalidKeyFormatException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;

/**
 * Created by pseillier on 28/12/2015.
 */
public class DictionaryUtilTest extends AbstractTest{

    private File testDir;
    private static final String ENCODING="UTF-8";

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        testDir = new File(getBaseDir()+"/src/test/resources/dictionaries");
    }
    @Test
    public void shouldFailDueToEmptyDictionary() throws Exception{
        File testFile = getTestFile("empty.dict");
        exception.expect(DictionaryException.class);
        exception.expectMessage("does not contain key/value pair");
        DictionaryUtil.readDictionaryAsEnvVariables(testFile,ENCODING);
    }

    @Test
    public void shouldFailDueToMalFormedKeyDictionary() throws Exception {
        File testFile = getTestFile("malformed-key-01.dict");
        exception.expect(DictionaryException.class);
        exception.expectCause(CoreMatchers.is(IsInstanceOf.<Throwable>instanceOf(InvalidKeyFormatException.class)));

        DictionaryUtil.readDictionaryAsEnvVariables(testFile,ENCODING);

    }

    @Test
    public void shouldFailDueToDictionaryWithoutKeyValue() throws Exception {
        File testFile = getTestFile("without-key-value.dict");
        exception.expect(DictionaryException.class);
        DictionaryUtil.readDictionaryAsEnvVariables(testFile,ENCODING);

    }

    private File getTestFile(String name) {

       return new File(testDir,name);
    }
}

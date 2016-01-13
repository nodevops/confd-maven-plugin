package com.github.nodevops.confd.maven.plugin.utils;

import com.github.nodevops.confd.maven.plugin.AbstractTest;
import org.codehaus.plexus.util.FileUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by pseillier on 28/12/2015.
 */
public class DictionaryUtilTest extends AbstractTest{

    private static final String ENCODING = "UTF-8";

    @Rule
    public ExpectedException exception = ExpectedException.none();
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder(new File(getBaseDir(),"target"));

    @Test
    public void shouldFailDueToEmptyDictionary() throws Exception {
        File testFile = temporaryFolder.newFile("test.dict");
        exception.expect(DictionaryException.class);
        exception.expectMessage("does not contain key/value pair");
        DictionaryUtil.readDictionaryAsEnvVariables(testFile, ENCODING);
    }

    @Test
    public void shouldFailDueToMalFormedKeyDictionary() throws Exception {
        String[] lines = {
            "/web/mediaserver/url=http://media01.server.com/",
            "",
            "# Missing the first /",
            "web/database/username=user01",
            "",
            "/web/database/password=pwd01",
        };


        File testFile = createTestFile(lines);

        exception.expect(DictionaryException.class);
        exception.expectCause(CoreMatchers.is(IsInstanceOf.<Throwable>instanceOf(InvalidKeyFormatException.class)));

        DictionaryUtil.readDictionaryAsEnvVariables(testFile, ENCODING);

    }

    @Test
    public void shouldFailDueToDictionaryWithoutKeyValue() throws Exception {
        String[] lines = {
            "# This file contains only comments and blank lines",
            "# One blank line",
            "",
            "# Two blank lines",
            "",
            "",
            "# End of file",
        };

        File testFile = createTestFile(lines);

        exception.expect(DictionaryException.class);
        exception.expectMessage("does not contain key/value pair");
        DictionaryUtil.readDictionaryAsEnvVariables(testFile, ENCODING);

    }

    @Test
    public void shouldFailDueToKeyWithoutValue() throws Exception {

        String[] lines = {
            "/web/mediaserver/url =",
        };

        File testFile = createTestFile(lines);

        exception.expect(DictionaryException.class);
        exception.expectMessage("bad key/value format for");

        DictionaryUtil.readDictionaryAsEnvVariables(testFile, ENCODING);

    }

    @Test
    public void shouldSuccessWithSpacesAroundEqualSeparator() throws Exception {
        String[] lines = {
            "/part1/part2   =   value1",
            "/part1/part3 = value2",
        };

        File testFile = createTestFile(lines);

        Map<String, String> env = DictionaryUtil.readDictionaryAsEnvVariables(testFile, ENCODING);
        assertThat(env).isNotEmpty();
        assertThat(env).containsEntry("PART1_PART2", "value1");
        assertThat(env).containsEntry("PART1_PART3", "value2");
    }

    @Test
    public void shouldTrimKeyName() throws Exception {
        String[] lines = {
            "/part1/part2   =   value1",
            "    /part1/part3  =   value2",
            "\t/part1/part4\t=\tthe value 3"
        };

        File testFile = createTestFile(lines);

        Map<String, String> env = DictionaryUtil.readDictionaryAsEnvVariables(testFile, ENCODING);
        assertThat(env).isNotEmpty();
        assertThat(env).containsEntry("PART1_PART2", "value1");
        assertThat(env).containsEntry("PART1_PART3", "value2");
        assertThat(env).containsEntry("PART1_PART4", "the value 3");
    }

    @Test
    public void shouldKeyEqualCharactersInValue() throws Exception {
        String[] lines = {
            "/part1/part2 = value1",
            "/part1/part3 = value2=toto",
            "/part1/part4 = the value 3 is = to 3"
        };

        File testFile = createTestFile(lines);

        Map<String, String> env = DictionaryUtil.readDictionaryAsEnvVariables(testFile, ENCODING);
        assertThat(env).isNotEmpty();
        assertThat(env).containsEntry("PART1_PART2", "value1");
        assertThat(env).containsEntry("PART1_PART3", "value2=toto");
        assertThat(env).containsEntry("PART1_PART4", "the value 3 is = to 3");
    }

    @Test
    public void shouldSuccessWithValidKeyValues() throws Exception {
        String[] lines = {
            "/part1/part11_sub1/part111 = value1",
            "/part2/part22_sub2/part222 = value2",
        };

        File testFile = createTestFile(lines);

        Map<String, String> env = DictionaryUtil.readDictionaryAsEnvVariables(testFile, ENCODING);

        assertThat(env).isNotEmpty();
        assertThat(env).containsEntry("PART1_PART11_SUB1_PART111", "value1");
        assertThat(env).containsEntry("PART2_PART22_SUB2_PART222", "value2");
    }

    private File createTestFile(String[] lines) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (String s : lines) {
            sb.append(s).append('\n');
        }
        File f = temporaryFolder.newFile("test.dict");
        FileUtils.fileWrite(f, ENCODING, sb.toString());
        return f;
    }
}

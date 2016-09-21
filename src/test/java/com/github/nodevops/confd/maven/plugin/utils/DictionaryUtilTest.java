package com.github.nodevops.confd.maven.plugin.utils;

import static com.github.nodevops.confd.maven.plugin.utils.DictionaryUtil.BAD_KEY_VALUE_FORMAT_FOR_LINE_MESSAGE;
import static com.github.nodevops.confd.maven.plugin.utils.DictionaryUtil.DOES_NOT_CONTAIN_KEY_VALUE_PAIR_MESSAGE;
import static com.github.nodevops.confd.maven.plugin.utils.DictionaryUtil.EMPTY_VALUES_ARE_NOT_ALLOWED_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Test;

import com.github.nodevops.confd.maven.plugin.AbstractTest;

public class DictionaryUtilTest extends AbstractTest {



    @Test
    public void shouldFailDueToEmptyDictionary() throws Exception {
        File testFile = temporaryFolder.newFile("test.dict");
        exception.expect(DictionaryException.class);
        exception.expectMessage(DOES_NOT_CONTAIN_KEY_VALUE_PAIR_MESSAGE);
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
        exception.expectMessage(DOES_NOT_CONTAIN_KEY_VALUE_PAIR_MESSAGE);
        DictionaryUtil.readDictionaryAsEnvVariables(testFile, ENCODING);

    }

    @Test
    public void shouldFailDueToKeyWithoutValue() throws Exception {

        String[] lines = {
            "/web/mediaserver/url =",
        };

        File testFile = createTestFile(lines);

        exception.expect(DictionaryException.class);
        exception.expectMessage(EMPTY_VALUES_ARE_NOT_ALLOWED_MESSAGE);

        DictionaryUtil.readDictionaryAsEnvVariables(testFile, ENCODING);

    }

    @Test
    public void shouldFailDueToLineWithoutEquals() throws Exception {

        String[] lines = {
            "/web/mediaserver/url",
        };

        File testFile = createTestFile(lines);

        exception.expect(DictionaryException.class);
        exception.expectMessage(BAD_KEY_VALUE_FORMAT_FOR_LINE_MESSAGE);

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
    public void shouldTrimKeyNamesAndValues() throws Exception {
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


}

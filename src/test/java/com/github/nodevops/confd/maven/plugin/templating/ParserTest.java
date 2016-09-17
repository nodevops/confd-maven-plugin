package com.github.nodevops.confd.maven.plugin.templating;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.junit.Test;

import com.github.nodevops.confd.maven.plugin.AbstractTest;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class ParserTest extends AbstractTest {
    @Test
    public void shouldSuccessWithSimpleTemplate() throws IOException {
        String[] templateLines = {
            "server:",
            "  port: {{getv \"/your/namespace/myapp/httpport\"}}",
            "tomcat:",
            "  accesslog:",
            "  directory: {{getv \"/your/namespace/myapp/tomcat/access/log/dir\"}}",
            "  enabled: {{getv \"/your/namespace/myapp/tomcat/access/log/enabled\"}}",
        };
        Map<String, String> env = ImmutableMap.<String, String>builder()
            .put("/your/namespace/myapp/httpport", "8080")
            .put("/your/namespace/myapp/tomcat/access/log/dir", "/var/log/appli")
            .put("/your/namespace/myapp/tomcat/access/log/enabled", "true")
            .build();
        File templateFile = createTestFile(templateLines);
        Parser parser = new Parser(templateFile, "UTF-8");
        String parsedTemplate = parser.parse(env);
        assertThat(parsedTemplate).isEqualTo("server:\n" +
            "  port: 8080\n" +
            "tomcat:\n" +
            "  accesslog:\n" +
            "  directory: /var/log/appli\n" +
            "  enabled: true\n");
    }

    @Test
    public void shouldFailBecauseMissingKey() throws IOException {
        String[] templateLines = {
            "server:",
            "  port: {{getv \"/your/namespace/myapp/httpport\"}}",
            "tomcat:",
            "  accesslog:",
            "  directory: {{getv \"/your/namespace/myapp/tomcat/access/log/dir\"}}",
            "  enabled: {{getv \"/your/namespace/myapp/tomcat/access/log/enabled\"}}",
        };
        Map<String, String> env = ImmutableMap.<String, String>builder()
            .put("/your/namespace/myapp/httpport", "8080")
            .put("/your/namespace/myapp/tomcat/access/log/dir", "/var/log/appli")
            .build();
        File templateFile = createTestFile(templateLines);
        Parser parser = new Parser(templateFile, "UTF-8");
        exception.expect(IOException.class);
        exception.expectMessage("test.dict [6,19] : key /your/namespace/myapp/tomcat/access/log/enabled does not exist");
        parser.parse(env);
    }

    @Test
    public void shouldFailBecauseUnfinishedCommandAtEOF() throws IOException {
        String[] templateLines = {
            "server:",
            "  port: {{getv \"/your/namespace/myapp/httpport\"}}",
            "tomcat:",
            "  accesslog:",
            "  directory: {{getv \"/your/namespace/myapp/tomcat/access/log/dir\"}}",
            "  enabled: {{getv \"/your/namespace/myapp/tomcat/access/log/enabled\"",
        };
        Map<String, String> env = ImmutableMap.<String, String>builder()
            .put("/your/namespace/myapp/httpport", "8080")
            .put("/your/namespace/myapp/tomcat/access/log/dir", "/var/log/appli")
            .put("/your/namespace/myapp/tomcat/access/log/enabled", "true")
            .build();
        File templateFile = createTestFile(templateLines);
        Parser parser = new Parser(templateFile, "UTF-8");
        exception.expect(IOException.class);
        exception.expectMessage("test.dict [6,67] : unexpected unclosed action in command");
        parser.parse(env);
    }

    @Test
    public void shouldFailBecauseUnfinishedCommandNotAtEOF() throws IOException {
        String[] templateLines = {
            "server:",
            "  port: {{getv \"/your/namespace/myapp/httpport\"}}",
            "tomcat:",
            "  accesslog:",
            "  directory: {{getv \"/your/namespace/myapp/tomcat/access/log/dir\"",
            "  enabled: {{getv \"/your/namespace/myapp/tomcat/access/log/enabled\"",
        };
        Map<String, String> env = ImmutableMap.<String, String>builder()
            .put("/your/namespace/myapp/httpport", "8080")
            .put("/your/namespace/myapp/tomcat/access/log/dir", "/var/log/appli")
            .put("/your/namespace/myapp/tomcat/access/log/enabled", "true")
            .build();
        File templateFile = createTestFile(templateLines);
        Parser parser = new Parser(templateFile, "UTF-8");
        exception.expect(IOException.class);
        exception.expectMessage("test.dict [5,65] : unexpected unclosed action in command");
        parser.parse(env);
    }

    @Test
    public void shouldFailBecauseUnfinishedCommand() throws IOException {
        String[] templateLines = {
            "server:",
            "  port: {{getv \"/your/namespace/myapp/httpport\"}}",
            "tomcat:",
            "  accesslog:",
            "  directory: {{getv \"/your/namespace/myapp/tomcat/access/log/dir\" anotherFunc }}",
            "  enabled: {{getv \"/your/namespace/myapp/tomcat/access/log/enabled\"}}",
        };
        Map<String, String> env = ImmutableMap.<String, String>builder()
            .put("/your/namespace/myapp/httpport", "8080")
            .put("/your/namespace/myapp/tomcat/access/log/dir", "/var/log/appli")
            .put("/your/namespace/myapp/tomcat/access/log/enabled", "true")
            .build();
        File templateFile = createTestFile(templateLines);
        Parser parser = new Parser(templateFile, "UTF-8");
        exception.expect(IOException.class);
        exception.expectMessage("test.dict [5,66] : expected }}  but found anotherFunc");
        parser.parse(env);
    }

    @Test
    public void shouldFailBecauseWrongNumberOfArgsForGetv() throws IOException {
        String[] templateLines = {
            "server:",
            "  port: {{getv \"/your/namespace/myapp/httpport\"}}",
            "tomcat:",
            "  accesslog:",
            "  directory: {{getv }}",
            "  enabled: {{getv \"/your/namespace/myapp/tomcat/access/log/enabled\"}}",
        };
        Map<String, String> env = ImmutableMap.<String, String>builder()
            .put("/your/namespace/myapp/httpport", "8080")
            .put("/your/namespace/myapp/tomcat/access/log/dir", "/var/log/appli")
            .put("/your/namespace/myapp/tomcat/access/log/enabled", "true")
            .build();
        File templateFile = createTestFile(templateLines);
        Parser parser = new Parser(templateFile, "UTF-8");
        exception.expect(IOException.class);
        exception.expectMessage("test.dict [5,20] : wrong number of args for 'getv': want 1 got 0");
        parser.parse(env);
    }

    @Test
    public void shouldFailBecauseWrongNumberOfArgsForGetvAtEOF() throws IOException {
        String[] templateLines = {
            "server:",
            "  port: {{getv \"/your/namespace/myapp/httpport\"}}",
            "tomcat:",
            "  accesslog:",
            "  directory: {{getv \"/your/namespace/myapp/tomcat/access/log/enabled\" }}",
            "  enabled: {{getv ",
        };
        Map<String, String> env = ImmutableMap.<String, String>builder()
            .put("/your/namespace/myapp/httpport", "8080")
            .put("/your/namespace/myapp/tomcat/access/log/dir", "/var/log/appli")
            .put("/your/namespace/myapp/tomcat/access/log/enabled", "true")
            .build();
        File templateFile = createTestFile(templateLines);
        Parser parser = new Parser(templateFile, "UTF-8");
        exception.expect(IOException.class);
        exception.expectMessage("test.dict [6,18] : wrong number of args for 'getv': want 1 got 0");
        parser.parse(env);
    }

    @Test
    public void shouldFailBecauseUnbalancedComma() throws IOException {
        String[] templateLines = {
            "server:",
            "  port: {{getv \"/your/namespace/myapp/httpport\"}}",
            "tomcat:",
            "  accesslog:",
            "  directory: {{getv \"/your/namespace/myapp/tomcat/access/log/dir}}",
            "  enabled: {{getv \"/your/namespace/myapp/tomcat/access/log/enabled\"}}",
        };
        Map<String, String> env = ImmutableMap.<String, String>builder()
            .put("/your/namespace/myapp/httpport", "8080")
            .put("/your/namespace/myapp/tomcat/access/log/dir", "/var/log/appli")
            .put("/your/namespace/myapp/tomcat/access/log/enabled", "true")
            .build();
        File templateFile = createTestFile(templateLines);
        Parser parser = new Parser(templateFile, "UTF-8");
        exception.expect(IOException.class);
        exception.expectMessage("test.dict [5,21] : unterminated quoted string");
        parser.parse(env);
    }

    @Test
    public void shouldFailBecauseUnsupportedFunction() throws IOException {
        String[] templateLines = {
            "server:",
            "  port: {{getv \"/your/namespace/myapp/httpport\"}}",
            "tomcat:",
            "  accesslog:",
            "  directory: {{anotherFunc \"/your/namespace/myapp/tomcat/access/log/dir\" }}",
            "  enabled: {{getv \"/your/namespace/myapp/tomcat/access/log/enabled\"}}",
        };
        Map<String, String> env = ImmutableMap.<String, String>builder()
            .put("/your/namespace/myapp/httpport", "8080")
            .put("/your/namespace/myapp/tomcat/access/log/dir", "/var/log/appli")
            .put("/your/namespace/myapp/tomcat/access/log/enabled", "true")
            .build();
        File templateFile = createTestFile(templateLines);
        Parser parser = new Parser(templateFile, "UTF-8");
        exception.expect(IOException.class);
        exception.expectMessage("test.dict [5,15] : function 'anotherFunc' not supported");
        parser.parse(env);
    }

    @Test
    public void shouldFailBecauseMissingSupportedFunction() throws IOException {
        String[] templateLines = {
            "server:",
            "  port: {{getv \"/your/namespace/myapp/httpport\"}}",
            "tomcat:",
            "  accesslog:",
            "  directory: {{ \"/your/namespace/myapp/tomcat/access/log/dir\" }}",
            "  enabled: {{getv \"/your/namespace/myapp/tomcat/access/log/enabled\"}}",
        };
        Map<String, String> env = ImmutableMap.<String, String>builder()
            .put("/your/namespace/myapp/httpport", "8080")
            .put("/your/namespace/myapp/tomcat/access/log/dir", "/var/log/appli")
            .put("/your/namespace/myapp/tomcat/access/log/enabled", "true")
            .build();
        File templateFile = createTestFile(templateLines);
        Parser parser = new Parser(templateFile, "UTF-8");
        exception.expect(IOException.class);
        exception.expectMessage("test.dict [5,17] : expected 'getv' but found something that looks like a key");
        parser.parse(env);
    }

    @Test
    public void shouldFailBecauseMissingValueForCommand() throws IOException {
        String[] templateLines = {
            "server:",
            "  port: {{getv \"/your/namespace/myapp/httpport\"}}",
            "tomcat:",
            "  accesslog:",
            "  directory: {{ }}",
            "  enabled: {{getv \"/your/namespace/myapp/tomcat/access/log/enabled\"}}",
        };
        Map<String, String> env = ImmutableMap.<String, String>builder()
            .put("/your/namespace/myapp/httpport", "8080")
            .put("/your/namespace/myapp/tomcat/access/log/dir", "/var/log/appli")
            .put("/your/namespace/myapp/tomcat/access/log/enabled", "true")
            .build();
        File templateFile = createTestFile(templateLines);
        Parser parser = new Parser(templateFile, "UTF-8");
        exception.expect(IOException.class);
        exception.expectMessage("test.dict [5,16] : missing value for command");
        parser.parse(env);
    }

    @Test
    public void shouldFailBecauseUnfinishedTemplateAtEOF() throws IOException {
        String[] templateLines = {
            "server:",
            "  port: {{getv \"/your/namespace/myapp/httpport\"}}",
            "tomcat:",
            "  accesslog:",
            "  directory: {{ getv \"/your/namespace/myapp/tomcat/access/log/dir\" }}",
            "  enabled: {{ ",
        };
        Map<String, String> env = ImmutableMap.<String, String>builder()
            .put("/your/namespace/myapp/httpport", "8080")
            .put("/your/namespace/myapp/tomcat/access/log/dir", "/var/log/appli")
            .put("/your/namespace/myapp/tomcat/access/log/enabled", "true")
            .build();
        File templateFile = createTestFile(templateLines);
        Parser parser = new Parser(templateFile, "UTF-8");
        exception.expect(IOException.class);
        exception.expectMessage("test.dict [6,14] : unexpected unclosed action in command");
        parser.parse(env);
    }

    @Test
    public void shouldGatherKeys() throws IOException {
        String[] templateLines = {
            "server:",
            "  port: {{getv \"/your/namespace/myapp/httpport\"}}",
            "tomcat:",
            "  accesslog:",
            "  directory: {{getv \"/your/namespace/myapp/tomcat/access/log/dir\"}}",
            "  enabled: {{getv \"/your/namespace/myapp/tomcat/access/log/enabled\"}}",
        };
        Map<String, String> env = Maps.newHashMap();
        File templateFile = createTestFile(templateLines);
        Parser parser = new Parser(templateFile, "UTF-8", Parser.ParserType.GATHER);
        parser.parse(env);
        assertThat(parser.getGatheredKeys()).containsOnly(
            "/your/namespace/myapp/httpport",
            "/your/namespace/myapp/tomcat/access/log/dir",
            "/your/namespace/myapp/tomcat/access/log/enabled");
    }
}

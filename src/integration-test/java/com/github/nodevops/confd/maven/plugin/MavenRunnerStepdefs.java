package com.github.nodevops.confd.maven.plugin;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.LogOutputStream;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.FileUtils;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;


/**
 * get Commons Exec output : http://stackoverflow.com/questions/7340452/process-output-from-apache-commons-exec/12301247#12301247
 */
public class MavenRunnerStepdefs {
    public static final String JAVA_HOME_ENV_KEY = "JAVA_HOME";
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String CHARSET_UTF_8 = "UTF-8";
    private Map<String, String> environment = Maps.newHashMap();
    private String m2Home;
    private String projectRoot;
    private File projectRootAsFile;
    private List<String> mvnArgs = Lists.newArrayList();
    private List<String> executorOutput = Lists.newArrayList();
    private String fullOutput;
    private String scenarioId;
    private int exitCode;
    private Map<String, String> fileContentCache = Maps.newHashMap();

    @Before({"@mavenBuild"})
    public void beforeMavenBuild(Scenario scenario) {
        this.scenarioId = scenario.getId();
        System.out.println("Launching a maven build inside maven because we can (scenario :" + scenarioId + ")");
        System.out.println("=====>>>>>>>");
    }

    @After({"@mavenBuild"})
    public void afterMavenBuild() {
        System.out.println("<<<<<===== end of a maven build (scenario " + scenarioId + ")");
    }

    @Given("JAVA_HOME is set to: (.*)")
    public void setJavaHome(String javaHome) {
        System.out.println("Setting JAVA_HOME to <" + javaHome + ">");
        environment.put(JAVA_HOME_ENV_KEY, javaHome);
    }

    @Given("M2_HOME is set to: (.*)")
    public void setMavenHome(String m2Home) {
        System.out.println("Setting M2_HOME to <" + m2Home + ">");
        this.m2Home = m2Home;
    }

    @Given("my test project root is : (.*)")
    public void setProjectRoot(String projectRoot) {
        System.out.println("Setting projectRoot to <" + projectRoot + ">");
        this.projectRoot = projectRoot;
        this.projectRootAsFile = new File(projectRoot);
        assertThat(this.projectRootAsFile).exists();
    }

    @When("I run maven with args: (.*)")
    public void runMavenCommand(List<String> mvnArgs) throws IOException {
        this.mvnArgs.addAll(mvnArgs);
        System.out.println("Launching Maven with args <" + Joiner.on(" ").join(mvnArgs) + ">");
        CommandLine cmdLine = new CommandLine(getCommandLine());
        for (String mvnArg : mvnArgs) {
            cmdLine.addArgument(mvnArg);
        }
        DefaultExecutor executor = new DefaultExecutor();
        if (projectRootAsFile != null) {
            executor.setWorkingDirectory(projectRootAsFile);
        }
        executor.setStreamHandler(new PumpStreamHandler(new LogOutputStream() {
            @Override
            protected void processLine(String line, int level) {
                System.out.println(line);
                executorOutput.add(line);
            }
        }));
        exitCode = executor.execute(cmdLine, environment);
        fullOutput = Joiner.on(LINE_SEPARATOR).join(executorOutput);
    }

    @Then("the build is OK")
    public void buildIsOk() {
        assertThat(exitCode).isEqualTo(0);
    }

    @Then("the build is not OK")
    public void buildIsNotOk() {
        assertThat(exitCode).isNotEqualTo(0);
    }

    @Then("output contains: (.*)")
    public void outputContains(String part) {
        assertThat(fullOutput).contains(part);
    }

    @Then("output does not contain: (.*)")
    public void outputDoesNotContain(String part) {
        assertThat(fullOutput).doesNotContain(part);
    }

    @Then("(\\d+) templates were found")
    public void someTemplatesWereFound(int nb) {
        outputContains("found <" + nb + "> template(s)");
    }

    @Then("1 template was found")
    public void oneTemplateWasFound() {
        someTemplatesWereFound(1);
    }

    @Then("file '(.*)' exists")
    public void fileExists(String path) {
        assertThat(new File(getFilePathInProject(path))).exists().canRead();
    }

    @Then("files exist:")
    public void filesExist(List<String> files) {
        for (String file : files) {
            fileExists(file);
        }
    }

    @Then("file '(.*)' contains string: '(.*)'")
    public void fileContainsLine(String file, String line) throws IOException {
        assertThat(getFileContent(file)).contains(line);
    }

    @Then("file '(.*)' contains:")
    public void fileContainsLines(String file, List<String> lines) throws IOException {
        for (String line : lines) {
            assertThat(getFileContent(file)).contains(line);
        }
    }

    @Then("file '(.*)' content is:")
    public void fileContentIs(String file, String content) throws IOException {
        assertThat(getFileContent(file)).isEqualTo(content);
    }

    String getFileContent(String file) throws IOException {
        if (!fileContentCache.containsKey(file)) {
            fileContentCache.put(
                file,
                Joiner
                    .on(LINE_SEPARATOR)
                    .join(
                        FileUtils.readLines(new File(getFilePathInProject(file)), CHARSET_UTF_8)));
        }
        return fileContentCache.get(file);
    }


    String getCommandLine() {
        if (m2Home == null) {
            return getMvnBinary();
        } else {
            return Joiner.on(FILE_SEPARATOR).join(m2Home, "bin", getMvnBinary());
        }
    }

    String getMvnBinary() {
        return TestUtils.isRunningOnWindows() ? "mvn.bat" : "mvn";
    }

    String getFilePathInProject(String path) {
        return Joiner.on(FILE_SEPARATOR).join(projectRoot, path);
    }

}

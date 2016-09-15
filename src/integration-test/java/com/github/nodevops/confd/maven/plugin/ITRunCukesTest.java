package com.github.nodevops.confd.maven.plugin;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = "json:target/cucumber-report.json")
public class ITRunCukesTest {
}

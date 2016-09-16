package com.github.nodevops.confd.maven.plugin;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = "json:target/cucumber-report.json", tags = {"~@skip"})
public class ITRunCukesTest {
}

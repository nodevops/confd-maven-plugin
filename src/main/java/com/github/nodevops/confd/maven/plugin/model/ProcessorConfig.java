package com.github.nodevops.confd.maven.plugin.model;

import java.util.Properties;

import lombok.Data;

@Data
public class ProcessorConfig {

    private String name;
    private Properties properties;

}

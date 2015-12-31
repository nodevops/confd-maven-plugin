package com.github.nodevops.confd.maven.plugin.model;

import lombok.Data;

import java.util.Properties;

/**
 * Created by pseillier on 21/12/2015.
 */
@Data
public class ProcessorConfig {

    private String name;
    private Properties properties;

}

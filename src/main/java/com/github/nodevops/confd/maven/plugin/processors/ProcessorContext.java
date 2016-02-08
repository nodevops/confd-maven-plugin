package com.github.nodevops.confd.maven.plugin.processors;

import java.io.File;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProcessorContext {
    private File dictionaryPath;
    private File workingDirectory;
    private String encoding;
    private boolean mkdirs;
}

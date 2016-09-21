package com.github.nodevops.confd.maven.plugin.utils;

import java.io.File;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PrepareContext {
    private String encoding;
    private File workingDirectory;
    private boolean skipPrepare;
}

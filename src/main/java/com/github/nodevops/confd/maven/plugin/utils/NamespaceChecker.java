package com.github.nodevops.confd.maven.plugin.utils;

import java.util.List;

import com.google.common.collect.Lists;

public class NamespaceChecker {
    private final List<String> keys;
    private final String[] namespaces;

    public NamespaceChecker(List<String> keys, String[] namespaces) {
        this.keys = keys;
        this.namespaces = namespaces;
    }

    public List<String> getUnmatchedKeys() {
        List<String> offendingKeys = Lists.newArrayList();
        for (String key : keys) {
            boolean found = false;
            for(String ns: namespaces) {
                if(key.startsWith(ns)) {
                    found = true;
                }
            }
            if(!found) {
                offendingKeys.add(key);
            }
        }
        return offendingKeys;
    }
}

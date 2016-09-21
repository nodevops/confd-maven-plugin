package com.github.nodevops.confd.maven.plugin.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

public class NamespaceCheckerTest {

    @Test
    public void testGetUnmatchedKeysWhenNoOffendingKeys() {
        List<String> keys = Lists.newArrayList(
            "/ns1/one",
            "/ns2/one",
            "/ns1/two",
            "/ns2/gna"
        );

        String[] namespaces = {"/ns1", "/ns2"};
        List<String> offending = new NamespaceChecker(keys, namespaces).getUnmatchedKeys();
        assertThat(offending).isEmpty();
    }

    @Test
    public void testGetUnmatchedKeysWhenOffendingKeys1() {
        List<String> keys = Lists.newArrayList(
            "/ns1/one",
            "/ns2/one",
            "/ns1/two",
            "/ns2/gna"
        );

        String[] namespaces = {"/ns1", "/ns3"};
        List<String> offending = new NamespaceChecker(keys, namespaces).getUnmatchedKeys();
        assertThat(offending).isNotEmpty().containsOnly("/ns2/one", "/ns2/gna");
    }

    @Test
    public void testGetUnmatchedKeysWhenOffendingKeys2() {
        List<String> keys = Lists.newArrayList(
            "/ns1/one",
            "/ns2/one",
            "/ns1/two",
            "/ns2/gna"
        );

        String[] namespaces = {"/ns2", "/ns3"};
        List<String> offending = new NamespaceChecker(keys, namespaces).getUnmatchedKeys();
        assertThat(offending).isNotEmpty().containsOnly("/ns1/one", "/ns1/two");
    }
}

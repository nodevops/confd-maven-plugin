package com.github.nodevops.confd.maven.plugin.processors;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.StringTokenizer;

import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

import com.github.nodevops.confd.maven.plugin.model.ProcessorConfig;
import com.github.nodevops.confd.maven.plugin.processors.impl.JavaProcessorImpl;
import com.github.nodevops.confd.maven.plugin.processors.impl.LocalConfdProcessorImpl;
import com.github.nodevops.confd.maven.plugin.processors.impl.RemoteConfdProcessorImpl;

public class ProcessorFactory {

    public static final String LOCAL_CONFD_PROCESSOR = "local-confd-processor";
    public static final String WS_CONFD_PROCESSOR = "ws-confd-processor";
    public static final String JAVA_PROCESSOR = "java-processor";

    public static Processor createProcessor(ProcessorConfig config) throws ProcessorCreationException {
        String processorName = config.getName();

        if (LOCAL_CONFD_PROCESSOR.equals(processorName)) {
            return createLocalConfdProcessor(config.getProperties());
        } else if (WS_CONFD_PROCESSOR.equals(processorName)) {
            return createWsConfdProcessor(config.getProperties());
        } else if (JAVA_PROCESSOR.equals(processorName)) {
            return createJavaProcessor();
        } else {
            throw new ProcessorCreationException("Unknow processor name : " + processorName +
                    " (available processors : local-confd-processor,remote-confd-processor,java-processor)");
        }
    }

    private static Processor createLocalConfdProcessor(Properties properties) throws ProcessorCreationException {
        String binaryPathProperty = properties.getProperty("binary.path");
        if (StringUtils.isEmpty(binaryPathProperty)) {
            throw new ProcessorCreationException("processor " + LOCAL_CONFD_PROCESSOR +
                    "the binay.path property is missing.");
        }
        // check if confd is present
        if (!FileUtils.fileExists(binaryPathProperty)) {
            throw new ProcessorCreationException("processor " + LOCAL_CONFD_PROCESSOR + " confd binary " +
                    binaryPathProperty + " does not exists");
        }
        Processor processor = new LocalConfdProcessorImpl(binaryPathProperty);
        return processor;
    }

    private static Processor createWsConfdProcessor(Properties properties) throws ProcessorCreationException {
        String url = properties.getProperty("url");
        if (StringUtils.isEmpty(url)) {
            throw new ProcessorCreationException("Unable to create remote-confd-processor : missing property 'url'");
        }
        try {
            URL httpURL = new URL(url);
            return new RemoteConfdProcessorImpl(httpURL);
        } catch (MalformedURLException e) {
            throw new ProcessorCreationException("Unable to create remote-confd-processor : bad property 'url' = " + url);
        }
    }

    private static Processor createJavaProcessor() {
        Processor processor = new JavaProcessorImpl();
        return processor;
    }

    private static String getConfdPathFromPATH() {
        // get the PATH
        String path = System.getenv("PATH");
        // get the path separator
        String pathSeparator;
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            pathSeparator = ";";
        } else {
            pathSeparator = ":";
        }

        // browse the PATH
        StringTokenizer st = new StringTokenizer(path, pathSeparator);
        while (st.hasMoreTokens()) {
            String dir = st.nextToken();
            File confdPath = new File(dir, "confd");
            if (confdPath.canExecute()) {
                return confdPath.getAbsolutePath();
            }
        }
        // no confd found in the PATH
        return null;
    }
}

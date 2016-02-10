package com.github.nodevops.confd.maven.plugin.mojo;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.github.nodevops.confd.maven.plugin.model.ProcessorConfig;
import com.github.nodevops.confd.maven.plugin.processors.Processor;
import com.github.nodevops.confd.maven.plugin.processors.ProcessorContext;
import com.github.nodevops.confd.maven.plugin.processors.ProcessorCreationException;
import com.github.nodevops.confd.maven.plugin.processors.ProcessorExecutionException;
import com.github.nodevops.confd.maven.plugin.processors.ProcessorFactory;
import com.github.nodevops.confd.maven.plugin.utils.FileUtils;

@Mojo(name = "process", defaultPhase = LifecyclePhase.PROCESS_RESOURCES, threadSafe = true)
public class ProcessMojo extends AbstractMojo {

    /**
     * The character encoding scheme to be applied when filtering resources.
     */
    @Parameter(property = "encoding", defaultValue = "${project.build.sourceEncoding}")
    protected String encoding;
    /**
     * The output directory into which the resources will be copied.
     */
    @Parameter(defaultValue = "${project.basedir}/target/confd", required = true)
    private File workingDirectory;

    /**
     * The confd processor to use. Can be either "local-confd-processor" to use a local confd binary, or
     * "java-processor" to use a very simple confd compatible parser written in java.
     */
    @Parameter(required = true)
    private ProcessorConfig processor;

    /**
     * The path to the file containing a list of key/value that are referenced in the templates
     */
    @Parameter(required = true)
    private File dictionary;

    @Parameter(defaultValue = "${project.basedir}", readonly = true)
    private File basedir;

    /**
     * If true (default), the plugin will create directories if needed in order to be able to create the target files
     */
    @Parameter(defaultValue = "true", property = "confd.mkdirs")
    private boolean mkdirs;

    /**
     * Set skipProcess to true on the command line if you want to disable the process goal
     */
    @Parameter(property = "confd.skipProcess", defaultValue = "false")
    private boolean skipProcess;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        if (skipProcess) {
            getLog().info("confd:process has been skipped per configuration of the 'confd.skipProcess' parameter.");
            return;
        }
        getLog().info("plugin configuration {" +
            " workingDirectory: " + workingDirectory +
            ", encoding:" + encoding +
            ", mkdirs:" + mkdirs +
            ", skipProcess:" + skipProcess +
            ", dictionary:" + dictionary +
            ", processor:" + processor.toString() +
            "}");
        dictionary = FileUtils.makeAbsoluteIfNeeded(dictionary, basedir);

        // the dictionary file must exists
        if (!dictionary.exists()) {
            throw new MojoExecutionException("dictionary " + dictionary + " does not exits");
        }

        // This is the real execution block
        try {
            // get the processor according to the <processor></processor> tag defined in the pom.xml
            Processor processor = ProcessorFactory.createProcessor(this.processor);
            ProcessorContext context = ProcessorContext.builder()
                .dictionaryPath(dictionary)
                .workingDirectory(workingDirectory)
                .encoding(encoding)
                .mkdirs(mkdirs)
                .build();

            getLog().info("Executing processor " + this.processor.getName());
            processor.process(context);
        } catch (ProcessorCreationException e) {
            throw new MojoExecutionException("Unable to create processor " + processor, e);
        } catch (ProcessorExecutionException e) {
            throw new MojoExecutionException("Unable to execute processor " + processor, e);
        }

    }

}

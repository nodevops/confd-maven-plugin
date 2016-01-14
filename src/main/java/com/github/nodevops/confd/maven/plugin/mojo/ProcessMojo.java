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
import com.github.nodevops.confd.maven.plugin.processors.ProcessorContextBuilder;
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

    @Parameter(required = true)
    private ProcessorConfig processor;

    @Parameter(required = true)
    private File dictionary;

    @Parameter(defaultValue = "${project.basedir}", readonly = true)
    private File basedir;

    @Parameter(property = "confd.skipProcess", defaultValue = "false")
    private boolean skipProcess;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        if (skipProcess) {
            getLog().info("confd:process has been skipped per configuration of the 'confd.skipProcess' parameter.");
            return;
        }
        getLog().info("confd:process execution");
        dictionary = FileUtils.makeAbsoluteIfNeeded(dictionary, basedir);

        // the dictionary file must exists
        if (!dictionary.exists()) {
            throw new MojoExecutionException("dictionary " + dictionary + " does not exits");
        }

        // This is the real execution block
        try {
            // get the processor according to the <processor></processor> tag defined in the pom.xml
            Processor processor = ProcessorFactory.createProcessor(this.processor);
            // build the processor execution context
            ProcessorContextBuilder processorContextBuilder = new ProcessorContextBuilder();

            ProcessorContext context = processorContextBuilder.dictionaryPath(dictionary)
                    .workingDirectory(workingDirectory)
                    .encoding(encoding)
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

package com.github.nodevops.confd.maven.plugin.mojo;

import com.github.nodevops.confd.maven.plugin.model.ProcessorConfig;
import com.github.nodevops.confd.maven.plugin.processors.*;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

/**
 * Created by pseillier on 21/12/2015.
 */
@Mojo(name = "process", defaultPhase = LifecyclePhase.PROCESS_RESOURCES, threadSafe = true)
public class ProcessMojo extends AbstractMojo {

    /**
     * The character encoding scheme to be applied when filtering resources.
     */
    @Parameter(property = "encoding", defaultValue = "${project.build.sourceEncoding}")
    protected String encoding;
    /**
     * The output directory into which to copy the resources.
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
            getLog().info("confd:process have been skipped per configuration of confd.skipProcess the  parameter.");
            return;
        }
        getLog().info("confd:process execution");
        // If the dictionary path is relative then add the project dir path to make an absolute path
        if (!dictionary.isAbsolute()) {
            dictionary = new File(basedir, dictionary.getPath());
        }

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

            getLog().info("Excecute processor " + this.processor.getName());
            processor.process(context);
        } catch (ProcessorCreationException e) {
            throw new MojoExecutionException("Unable to create processor " + processor, e);
        } catch (ProcessorExecutionException e) {
            throw new MojoExecutionException("Unable to execute processor " + processor, e);
        }

    }


}

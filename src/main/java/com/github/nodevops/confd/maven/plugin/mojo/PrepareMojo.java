package com.github.nodevops.confd.maven.plugin.mojo;

import com.github.nodevops.confd.maven.plugin.model.TemplateConfig;
import com.github.nodevops.confd.maven.plugin.utils.WorkingDirectoryUtil;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by pseillier on 21/12/2015.
 */
@Mojo(name = "prepare", defaultPhase = LifecyclePhase.GENERATE_RESOURCES, threadSafe = false)
public class PrepareMojo extends AbstractMojo {

    /**
     * The output directory into which to copy the resources.
     */
    @Parameter(defaultValue = "${project.basedir}/target/confd", required = true)
    private File workingDirectory;

    @Parameter(required = true)
    private List<TemplateConfig> templates;

    @Parameter(defaultValue = "${project.basedir}", readonly = true)
    private File basedir;


    @Parameter(property = "confd.skipPrepare", defaultValue = "false")
    private boolean skipPrepare;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        if (skipPrepare) {
            getLog().info("confd:prepare have been skipped per configuration of confd.skipPrepare parameter.");
            return;
        }
        getLog().info("confd:prepare execution");


        // configure templates full path if necessary
        for (TemplateConfig t : templates) {
            // the template source path can be relative to the ${project.basedir}
            if (!t.getSrc().isAbsolute()) {
                t.setSrc(new File(basedir, t.getSrc().getPath()));
            }
            // the source template file must exist !!
            if (!t.getSrc().exists()) {
                throw new MojoExecutionException("template src " + t.getSrc() + " does not exits");
            }

            // the template destination path can be relative to the ${project.basedir}
            if (!t.getDest().isAbsolute()) {
                t.setDest(new File(basedir, t.getDest().getPath()));
            }
            // The destination path can contain ${...} expressions. In this case the expressions will be handled by maven
            // and replaced by the corresponding values
            // for example : ${project.basedir}/target/confd/file.properties will be converted by maven to <Absolute path of the maven project>/target/confd/file.properties
            // If the expression is not handled by maven it will remain in the path. For example  ${unknown.property}/target/confd/file.properties will remain ${unknown.property}/target/confd/file.properties
            // As the plugin create the output directories it will create  ${unknown.property}/target/confd directory. This is a bad behavior.
            // So if the final path contains a ${...} expression the plugin must throw an exception
            if (Pattern.matches(".*\\$\\{.*\\}.*", t.getDest().getPath())) {
                throw new MojoExecutionException("template dest " + t.getDest() + " is not a valid path");
            }

        }

        // This is the real execution block
        try {

            // generate the "confd like" working directory which will contain the toml and template files
            WorkingDirectoryUtil.generateConfdArtefacts(workingDirectory, templates);

        } catch (IOException e) {
            throw new MojoExecutionException("Unable to generate confd artefacts in " + workingDirectory + " for templates " + templates, e);
        }

    }


}

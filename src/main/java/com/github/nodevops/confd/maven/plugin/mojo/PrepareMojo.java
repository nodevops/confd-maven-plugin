package com.github.nodevops.confd.maven.plugin.mojo;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.github.nodevops.confd.maven.plugin.model.TemplateConfig;
import com.github.nodevops.confd.maven.plugin.utils.WorkingDirectoryUtil;

@Mojo(name = "prepare", defaultPhase = LifecyclePhase.GENERATE_RESOURCES, threadSafe = false)
public class PrepareMojo extends AbstractMojo {

    public static final String INDEX_AND_ID_SEPARATOR = ":";
    public static final String ELEMENTS_SEPARATOR = ",";
    /**
     * The output directory into which to copy the resources.
     */
    @Parameter(defaultValue = "${project.basedir}/target/confd", required = true)
    private File workingDirectory;

    /**
     * A list of TemplateConfig elements. A TemplateConfig element mimics the content of a confd TOML file (see
     * https://github.com/kelseyhightower/confd/blob/master/docs/quick-start-guide.md#create-a-template-resource-config)
     * so you can specify the src of the template to parse (src), the destination (dest) and a list of keys
     * (full list of main namespaces) that are needed by the template. See the examples pages for more details.
     */
    @Parameter(required = true)
    private List<TemplateConfig> templates;

    /**
     * by default, the <i>dest</i> param is a String that will be preserved as-is. If you set
     * <i>forceDestToLocalFileSystemType</i> to true, then it will be transformed as a valid path according to the
     * OS where the current build is running (for instance, /a/unix/path will be transformed in \\a\\unix\\path under
     * windows)
     */
    @Parameter(property = "confd.forceDestToLocalFileSystemType", defaultValue = "false")
    private boolean forceDestToLocalFileSystemType;

    @Parameter(defaultValue = "${project.basedir}", readonly = true)
    private File basedir;

    /**
     * Set skipPrepare to true on the command line if you want to disable the prepare goal
     */
    @Parameter(property = "confd.skipPrepare", defaultValue = "false")
    private boolean skipPrepare;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        if (skipPrepare) {
            getLog().info("confd:prepare has been skipped per configuration of the confd.skipPrepare parameter.");
            return;
        }
        getLog().info("plugin configuration {" +
            " forceDestToLocalFileSystemType:" + forceDestToLocalFileSystemType +
            ", workingDirectory: " + workingDirectory +
            ", skipPrepare:" + skipPrepare +
            "}");
        checkRequirements();
        getLog().info("found <" + templates.size() + "> template(s):");
        for (TemplateConfig templateConfig : templates) {
            getLog().info(templateConfig.toString());
        }

        // This is the real execution block
        try {
            // generate the "confd like" working directory which will contain the toml and template files
            WorkingDirectoryUtil.generateConfdArtefacts(workingDirectory, templates, forceDestToLocalFileSystemType);
        } catch (IOException e) {
            throw new MojoExecutionException("Caught an IOException while trying to generate confd artefacts in " +
                workingDirectory + " for templates " + templates, e);
        }
    }

    void checkRequirements() throws MojoExecutionException {
        // configure templates full path if necessary
        int index = 0;
        for (TemplateConfig t : templates) {

            if (t.getSrc() == null) {
                throw new MojoExecutionException("template src" +
                    " cannot be null for Template element with index <" + index + "> and id <" + t.getId() + ">");
            }
            if (!t.getSrc().isAbsolute()) {
                t.setSrc(new File(basedir, t.getSrc().getPath()));
            }
            // the source template file must exist !!
            if (!t.getSrc().exists()) {
                throw new MojoExecutionException("template src <" + t.getSrc() +
                    "> does not exits for Template element with index <" + index + "> and id <" + t.getId() + ">");
            }

            if (t.getDest() == null) {
                throw new MojoExecutionException("template dest " +
                    " cannot be null for Template element with index <" + index + "> and id <" + t.getId() + ">");
            }
            // The destination path can contain ${...} expressions. In this case the expressions will be handled by maven
            // and replaced by the corresponding values
            // for example : ${project.basedir}/target/confd/file.properties will be converted by maven to <Absolute path of the maven
            // project>/target/confd/file.properties
            // If the expression is not handled by maven it will remain in the path. For example
            // ${unknown.property}/target/confd/file.properties will remain ${unknown.property}/target/confd/file.properties
            // As the plugin create the output directories it will create ${unknown.property}/target/confd directory. This is a bad
            // behavior.
            // So if the final path contains a ${...} expression the plugin must throw an exception
            if (Pattern.matches(".*\\$\\{.*\\}.*", t.getDest())) {
                throw new MojoExecutionException("template dest " + t.getDest() +
                    " is not a valid path for Template element with index <" + index + "> and id <" + t.getId() + ">");
            }
            index++;
        }
    }
}

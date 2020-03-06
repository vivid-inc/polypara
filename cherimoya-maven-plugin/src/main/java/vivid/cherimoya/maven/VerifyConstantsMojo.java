/*
 * Copyright 2017 The Cherimoya Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package vivid.cherimoya.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.i18n.I18N;
import vivid.cherimoya.annotation.Constant;

import java.io.File;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;

/*
https://stackoverflow.com/questions/11341783/accessing-classes-in-custom-maven-reporting-plugin
 */

/**
 * @since 1.0
 */
@Mojo(
        name = "verify",
        defaultPhase = LifecyclePhase.PROCESS_CLASSES
)
public class VerifyConstantsMojo
        extends AbstractMojo
{

    @Component
    private I18N i18n;

    /**
     * The Maven Project Object
     */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    /**
     * Expect these artifact versions to be available Process artifacts of these versions.
     * An error will be generated if a given artifact version is not available in the local repository.
     *
     * @since 1.0
     */
    @Parameter
    private String[] requireVersions;

    /**
     * Flag to easily skip execution.
     *
     * @since 1.0
     */
    @Parameter(property = Static.POM_CHERIMOYA_CONSTANTS_SKIP_PROPERTY_KEY, defaultValue = "false")
    private boolean skip;

    @Parameter
    private boolean verbose; // TODO

    public void execute()
            throws MojoExecutionException
    {
        final ExecutionContext executionContext = new ExecutionContext(
                i18n,
                getLog(),
                project
        );

        if (skip) {
            executionContext.log.info("vivid.cherimoya.action.skipping-execution-via-configuration");
            return;
        }

        try {
            // The set of JARs targeted for scanning are:
            //     The current project  UNION  Versions listed in the plugin configuration
            final URLClassLoader classLoader = URLClassLoader.newInstance(
                    new URL[]{new File(project.getBuild().getOutputDirectory()).toURI().toURL()}
            );

            // Sweeping across the subject versions, scan all .class files.
            // Build a DB of all fields annotated with @Constant, recording the (GAV, field reference, field value).
            final Set<Field> annotatedFields = ReflectiveScanner.scan(
                    Constant.class,
                    classLoader
            );

            System.out.println("** FIELDS");
            for (final Field f : annotatedFields) {
                System.out.println(f);
            }
        } catch (MalformedURLException e) {
            executionContext.log.error("vivid.cherimoya.error.ce-1-internal-error"); // TODO
        }

        // Analyze the progression of @Constant values thru the versions.
        // Emit build-breaking ERRORs on discontinuities.
        // Clarify the ordering, and document it.



        /*

        final ClassFileScanner classFileScanner = new ClassFileScanner(
                executionContext,
                project.getModel().getVersion()
        );

        if (versions.size() == 1) {
            executionContext.log.warn(
                    "vivid.cherimoya.warning.cw-1-skipping-execution-due-to-singular-version",
                    versions.iterator().next(),
                    mavenGAOf(project)
            );
        }

        artifactMetadataSource.retrieveAvailableVersions(
                artifact,
                localRepository,
                Collections.<ArtifactRepository>emptyList()
        );

        */
    }

}

// (rm -rf target ; mvn install && cd target/it/simple-it/ && mvn vivid.cherimoya:cherimoya-maven-plugin:1.0:verify)

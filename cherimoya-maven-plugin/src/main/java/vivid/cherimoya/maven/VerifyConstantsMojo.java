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

import io.vavr.collection.Stream;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.i18n.I18N;
import vivid.cherimoya.annotation.Constant;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Set;

/**
 * @since 1.0
 */
@Mojo(
        name = Static.POM_CHERIMOYA_VERIFY_MOJO_NAME,
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
    private List<String> requireVersions; // TODO Rename to "versions"

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
        if (requireVersions == null || requireVersions.isEmpty()) {
            executionContext.log.warn(
                    "vivid.cherimoya.warning.cw-1-skipping-execution-due-to-singular-version",
                    project.getVersion(),
                    Static.mavenGAOf(project)
            );
            return;
        }

        // TODO Silently filter out the current version from requireVersions

        executionContext.log.info(
                "vivid.cherimoya.action.verifying-constants",
                Static.mavenGAOf(project),
                Stream.ofAll(requireVersions)
                        .append(project.getVersion())
                        .intersperse(" ")
                        .fold("", String::concat)
        );

        try {
            // The set of JARs targeted for scanning are:
            //     The current project  UNION  Versions listed in the plugin configuration
            final Stream<Set<Field>> annotatedFields =
                    Stream.of(
                            Static.classLoaderForDirectory(project.getBuild().getOutputDirectory())
                    )
                            .map(cl -> ReflectiveScanner.scan(Constant.class, cl))
                    ;

            // Sweeping across the subject versions, scan all .class files.
            // Build DB of all fields annotated with @Constant, recording (GAV, field reference, field value).

            System.out.println("** FIELDS");
            annotatedFields.forEach(
                    s -> s.forEach(
                            System.out::println
                    )
            );
        } catch (MalformedURLException e) {
            executionContext.log.error("vivid.cherimoya.error.ce-1-internal-error"); // TODO
        }

        // Analyze the progression of @Constant values thru the versions.
        // Emit build-breaking ERRORs on discontinuities.
        // Clarify the ordering, and document it.



        /*
        artifactMetadataSource.retrieveAvailableVersions(
                artifact,
                localRepository,
                Collections.<ArtifactRepository>emptyList()
        );
        */
    }

}

// (rm -rf target ; mvn install && cd target/it/simple-it/ && mvn vivid.cherimoya:cherimoya-maven-plugin:1.0:verify)

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

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.i18n.I18N;

import java.util.*;

/*
TODO Design:

The set of versions targeted for scanning are the current project UNION the versions listed in the plugin configuration.

Sweeping across the subject versions, scan all .class files. Build a DB of all fields annotated with @Constant, recording the (GAV, field reference, field value).

Analyze the progression of @Constant values thru the versions. Clarify the ordering, and document it. Emit build-breaking ERRORs on discontinuities.

https://stackoverflow.com/questions/11341783/accessing-classes-in-custom-maven-reporting-plugin
TODO Identify all available versions of this G:A, known to the running Maven system. Does this mean the available versions in the localRepository and the remoteRepository's? artifactMetadataSource.retrieveAvailableVersions(artifact, localRepository, Collections.<ArtifactRepository>emptyList());

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

    // TODO Investigate refactoring requireVersions to 1..* of versionSet name=""
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
        final Factory factory = new Factory(
                i18n,
                getLog(),
                project
        );

        if (skip) {
            factory.log.info("vivid.cherimoya.action.skipping-execution-via-configuration");
            return;
        }

        factory.data.recordArtifactVersion(project.getModel().getVersion());

        final ClassFileScanner classFileScanner = new ClassFileScanner(
                factory,
                project.getModel().getVersion()
        );
        try {
            classFileScanner.scanAll();
        } catch (final DependencyResolutionRequiredException e) {
            factory.log.error("vivid.cherimoya.error.ce-1-internal-error", e);
        }

        final String ga = String.format(
                "%s:%s",
                project.getModel().getGroupId(),
                project.getModel().getArtifactId()
        );

        final Set<String> versions = factory.data.getAllVersions();
        if (versions.size() == 1) {
            factory.log.warn(
                    "vivid.cherimoya.warning.cw-1-skipping-execution-due-to-singular-version",
                    versions.iterator().next(),
                    ga
            );
        }
    }

}

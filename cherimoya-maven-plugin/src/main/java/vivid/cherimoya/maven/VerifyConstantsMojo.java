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

import io.vavr.Tuple2;
import io.vavr.collection.List;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.i18n.I18N;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;
import vivid.cherimoya.annotation.Constant;

/**
 * @since 1.0
 */
@Mojo(
        name = Static.POM_CHERIMOYA_VERIFY_MOJO_NAME,
        defaultPhase = LifecyclePhase.PROCESS_CLASSES
)
public class VerifyConstantsMojo extends AbstractMojo {

    @Component
    private I18N i18n;

    /**
     * The entry point to Maven Artifact Resolver, i.e. the component doing all the work.
     */
    @Component
    private RepositorySystem repositorySystem;

    /**
     * The current repository/network configuration of Maven.
     */
    @Parameter(defaultValue = "${repositorySystemSession}", readonly = true)
    private RepositorySystemSession repositorySystemSession;

    /**
     * The Maven Project Object
     */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject mavenProject;

    /**
     * The project's remote repositories to use for the resolution.
     */
    @Parameter(defaultValue = "${project.remoteProjectRepositories}", readonly = true)
    private java.util.List<RemoteRepository> remoteRepositories;

    /**
     * Expect these artifact versions to be available Process artifacts of these versions.
     * An error will be generated if a given artifact version is not available in the local repository.
     *
     * @since 1.0
     */
    @Parameter(alias = "versions")
    private java.util.List<String> specifiedVersions;

    /**
     * Flag to easily skip execution.
     *
     * @since 1.0
     */
    @Parameter(property = Static.POM_CHERIMOYA_CONSTANTS_SKIP_PROPERTY_KEY, defaultValue = "false")
    private boolean skip;

    public void execute() throws MojoExecutionException {
        final I18nContext i18NContext = new I18nContext(i18n);

        if (skip) {
            getLog().info(i18NContext.getText(
                    "vivid.cherimoya.action.skipping-execution-via-configuration")
            );
            return;
        }

        // There are 3 notions of versions to keep track of:
        //
        // A) The current Maven project's version. This may or may not be the most recent version.
        // B) All resolvable versions, from either the user's local or remote Maven repositories.
        // C) The union of these two: all versions under consideration.
        final List<String> allVersions = List
                .ofAll(specifiedVersions != null ? specifiedVersions : List.empty())
                .append(mavenProject.getVersion())
                .distinct();
        final List<String> resolvableVersions = allVersions.remove(mavenProject.getVersion());

        if (allVersions.size() == 1) {
            getLog().warn(i18NContext.getText(
                    "vivid.cherimoya.warning.cw-1-skipping-execution-due-to-singular-version",
                    mavenProject.getVersion(),
                    Static.mavenGAOf(mavenProject))
            );
            // TODO return;
        }

        getLog().info(i18NContext.getText(
                "vivid.cherimoya.action.verifying-constants",
                allVersions.size(),
                Static.mavenGAOf(mavenProject),
                Static.listOfVersions(allVersions)
        ));

        final ExecutionContext executionContext = new ExecutionContext(
                i18NContext,
                getLog(),
                mavenProject,
                remoteRepositories,
                repositorySystem,
                repositorySystemSession
        );

        //
        // Main processing pipeline
        //

        // The outer try-catch statement intercepts any sneakily-thrown exceptions, unwrapping
        // such exceptions in the catch block.
        try (
                // Instantiate a new graph DB to store processing data.
                final ConstantsData constantsData = new ConstantsGraphImpl(executionContext, allVersions)
        ) {
            // Map each version to a Jar file.
            // This Maven goal processes all versions under consideration, both implied (the project) and explicit
            // (configured in the plugin section in the POM).
            ArtifactResolution.mapVersionsToClassLoaders(executionContext, resolvableVersions)
                    .map((v, cr) -> new Tuple2<>(v, AsmScanner.scan(
                            new AsmFieldAnnotationScanner(executionContext, Constant.class),
                            List.ofAll(cr)
                    )))
                    .forEach(constantsData::recordConstantFields);
            // TODO Compute constancy, report.
        } catch (final Exception ex) {
            SneakyMojoException.unwrapMaybe(ex);
        }
    }

}

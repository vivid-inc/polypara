/*
 * Copyright 2017 The Cherimoya Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package vivid.cherimoya.maven;

import io.vavr.Tuple2;
import io.vavr.collection.List;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
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
@org.apache.maven.plugins.annotations.Mojo(
        name = Static.POM_CHERIMOYA_VERIFY_MOJO_NAME,
        defaultPhase = LifecyclePhase.PROCESS_CLASSES
)
public class VerifyConstantsMojo extends AbstractMojo implements Mojo {


    //
    // Mojo configuration
    //

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
    @Parameter(alias = Static.POM_CHERIMOYA_VERSIONS_CONFIGURATION_KEY)
    private java.util.List<String> specifiedVersions;

    /**
     * Flag to easily skip execution.
     *
     * @since 1.0
     */
    @Parameter(property = Static.POM_CHERIMOYA_SKIP_PROPERTY_KEY, defaultValue = "false")
    private boolean skip;

    enum ReportingLevel {
        WARNING,
        ERROR
    }
    @Parameter(property = Static.POM_CHERIMOYA_REPORTING_LEVEL_CONFIGURATION_KEY, defaultValue = "ERROR")
    private ReportingLevel reportingLevel;

    private I18nContext i18nContext;


    //
    // Mojo getters of execution context
    //

    @Override
    public I18nContext getI18nContext() {
        return i18nContext;
    }

    @Override
    public MavenProject getMavenProject() {
        return mavenProject;
    }

    @Override
    public ReportingLevel getReportingLevel() {
        return reportingLevel;
    }

    @Override
    public java.util.List<RemoteRepository> getRemoteRepositories() {
        return remoteRepositories;
    }

    @Override
    public RepositorySystem getRepositorySystem() {
        return repositorySystem;
    }

    @Override
    public RepositorySystemSession getRepositorySystemSession() {
        return repositorySystemSession;
    }


    public void execute() throws MojoExecutionException, MojoFailureException {
        i18nContext = new I18nContext(i18n);

        if (skip) {
            getLog().info(
                    i18nContext.getText(
                            "vivid.cherimoya.action.skipping-execution-via-configuration",
                            Static.POM_CHERIMOYA_SKIP_PROPERTY_KEY
                    )
            );
            return;
        }

        // There are 3 notions of versions to keep track of:
        //
        // A) The current Maven project's version. This may or may not be the most recent version.
        //    Available in {@code mavenProject}.
        //
        // B) All resolvable versions, from either the user's local or remote Maven repositories.
        //    These versions are specified as plugin parameters in the POM, available via the
        //    {@code specifiedVersions} parameter.
        //    All resolvable versions exclude the current Maven project version, even if it
        //    explicitly included in the POM plugin configuration.
        //
        // C) The union of these two, being all versions under consideration.
        //
        final List<String> allVersions = List
                .ofAll(specifiedVersions != null ? specifiedVersions : List.empty())
                .append(mavenProject.getVersion())
                .distinct();
        final List<String> resolvableVersions = allVersions.remove(mavenProject.getVersion());

        if (allVersions.size() == 1) {
            getLog().warn(i18nContext.getText(
                    "vivid.cherimoya.warning.cw-1-skipping-execution-due-to-singular-version",
                    mavenProject.getVersion(),
                    Static.mavenGAOf(mavenProject))
            );
            return;
        }

        getLog().info(i18nContext.getText(
                "vivid.cherimoya.action.verifying-constants",
                allVersions.size(),
                Static.mavenGAOf(mavenProject),
                Static.listOfVersions(allVersions)
        ));


        //
        // Processing pipeline of the "verify" goal
        //

        // The outer try-catch statement intercepts any sneakily-thrown exceptions, unwrapping
        // such exceptions in the catch block. This sneakiness reduces clutter from exception
        // handling code, enabling crisper functional style.
        try (
                // Instantiate a new graph DB to store processing data.
                final ConstantsData constantsData = new ConstantsGraphImpl(this, allVersions)
        ) {
            // Record @Constant fields:
            //
            // Map each version to a path to the corresponding Jar file within the local
            // Maven repository. Maven might be required to download the Jar file and store
            // it in the local repository beforehand.
            // All versions under consideration are thus processed, both implied (the project)
            // and explicit (configured in the plugin section in the POM).
            ArtifactResolution.mapVersionsToClassLoaders(this, resolvableVersions)

                    // Given a mapping from version strings to Jar file paths, scan each of
                    // the Java class files within the Jars, looking for fields annotated
                    // with our Constant annotation.
                    .map((v, cr) -> new Tuple2<>(v, AsmScanner.scan(
                            new AsmFieldAnnotationScanner(this, Constant.class),
                            List.ofAll(cr)
                    )))

                    // Store found fields and their values per version in the database.
                    .forEach(constantsData::recordConstantFields);
            getLog().info(
                    i18nContext.getText(
                            "vivid.cherimoya.action.found-n-constant-fields",
                            constantsData.constantFieldsCount(),
                            Static.mavenGAOf(mavenProject)
                    )
            );


            // Compute @Constant violations:
            //
            // Examine the constancy of each field's value across versions, and report violations.
            final List<ConstancyViolation> violations = constantsData.constancyViolationDescriptions();


            // In the event of violations, report them and fail the build:
            //
            Report.report(this, violations);
            if (!violations.isEmpty() && reportingLevel == ReportingLevel.ERROR) {
                throw new MojoFailureException(
                        i18nContext.getText(
                                "vivid.cherimoya.error.ce-2-field-value-constancy-verification-failed"
                        )
                );
            }
        } catch (final SneakyMojoException ex) {
            SneakyMojoException.unwrap(ex);
        } catch (final MojoFailureException ex) {
            throw ex;
        } catch (final Exception ex) {
            throw new MojoExecutionException(
                    i18nContext.getText(
                            "vivid.cherimoya.error.ce-1-internal-error",
                            "Unexpected exception"
                    ),
                    ex
            );
        }
    }

}

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

import io.vavr.collection.Set;
import io.vavr.collection.TreeSet;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataRetrievalException;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.i18n.I18N;

import java.util.List;

import static vivid.cherimoya.maven.Static.MAVEN_VERSION_RANGE_ENTIRE_RANGE;

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

    @Component
    private ArtifactFactory artifactFactory;

    @Component
    private ArtifactMetadataSource artifactMetadataSource;

    /**
     * The Maven Project Object
     */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    /**
     * In the context of the executing user's shell environment, the local repository would refer
     * to i.e. "~/.m2/repository". During automated testing, this is null and so they need to
     * create a local repository themselves.
     */
    @Parameter(property = "localRepository", readonly = true)
    protected ArtifactRepository localRepository;

    @Parameter(defaultValue = "${project.remoteArtifactRepositories}", readonly = true, required = true)
    private List<ArtifactRepository> remoteArtifactRepositories;

    /**
     * Expect these artifact versions to be available Process artifacts of these versions.
     * An error will be generated if a given artifact version is not available in the local repository.
     *
     * @since 1.0
     */
    @Parameter(alias = "versions")
    private List<String> specifiedVersions;

    /**
     * Flag to easily skip execution.
     *
     * @since 1.0
     */
    @Parameter(property = Static.POM_CHERIMOYA_CONSTANTS_SKIP_PROPERTY_KEY, defaultValue = "false")
    private boolean skip;

    public VerifyConstantsMojo() {}

    public void execute()
            throws MojoExecutionException
    {
        final I18nContext i18NContext = new I18nContext(i18n);

        if (skip) {
            getLog().info(i18NContext.getText(
                    "vivid.cherimoya.action.skipping-execution-via-configuration")
            );
            return;
        }

        // Clarify the notions of: current version, remotely-resolvable versions, all versions under consideration.
        final TreeSet<String> allVersionsAsStrings = TreeSet.ofAll(specifiedVersions).add(project.getVersion());

        if (allVersionsAsStrings.size() == 1) {
            getLog().warn(i18NContext.getText(
                    "vivid.cherimoya.warning.cw-1-skipping-execution-due-to-singular-version",
                    project.getVersion(),
                    Static.mavenGAOf(project))
            );
            return;
        }

        getLog().info(i18NContext.getText(
                "vivid.cherimoya.action.verifying-constants",
                allVersionsAsStrings.size(),
                Static.mavenGAOf(project),
                Static.listOfVersions(allVersionsAsStrings))
        );

        // Processing step:
        //
        // The set of JARs targeted for scanning are:
        //     The current project  UNION  Versions listed in the plugin configuration

        final TreeSet<ArtifactVersion> remotelyResolvableVersions = getArtifactVersionsFor(
                i18NContext,
                project.getGroupId(),
                project.getArtifactId()
        );
        final Set<String> remotelyResolvableVersionsAsStrings = remotelyResolvableVersions.toStream()
                .map(ArtifactVersion::toString)
                .toSet();

        final TreeSet<String> missingVersions = allVersionsAsStrings.diff(remotelyResolvableVersionsAsStrings);
        if (!missingVersions.isEmpty()) {
            throw new MojoExecutionException(
                    i18NContext.getText(
                            "vivid.cherimoya.error.ce-2-unresolved-artifact-versions",
                            Static.listOfVersions(missingVersions))
            );
        }


        // Processing step:
        //
        // Sweeping across the subject versions, scan all .class files.
        // Build DB of all fields annotated with @Constant, recording (GAV, field reference, field value).



        // Processing step:
        //
        // Analyze the progression of @Constant values thru the versions.
        // Emit build-breaking ERRORs on discontinuities.
        // Clarify the ordering, and document it.

    }

    TreeSet<ArtifactVersion> getArtifactVersionsFor(
            final I18nContext i18nContext,
            final String groupId,
            final String artifactId
    ) throws MojoExecutionException {
        try {
            final Artifact artifact = artifactFactory.createArtifact(
                    groupId,
                    artifactId,
                    MAVEN_VERSION_RANGE_ENTIRE_RANGE,
                    "", ""
            );
            final List<ArtifactVersion> artifactVersions = artifactMetadataSource.retrieveAvailableVersions(
                    artifact,
                    localRepository,
                    remoteArtifactRepositories
            );
            return TreeSet.ofAll(artifactVersions);
        } catch (final ArtifactMetadataRetrievalException e) {
            throw new MojoExecutionException(
                    i18nContext.getText("vivid.cherimoya.error.ce-1-internal-error"),
                    e
            );
        }
    }





    void x() {
        // The set of JARs targeted for scanning are:
        //     The current project  UNION  Versions listed in the plugin configuration
//            final Stream<Set<Field>> annotatedFields =
//                    Stream.of(
//                            Static.classLoaderForDirectory(project.getBuild().getOutputDirectory())
//                    )
//                            .map(cl -> ReflectiveScanner.scan(Constant.class, cl))
//                    ;

        // Sweeping across the subject versions, scan all .class files.
        // Build DB of all fields annotated with @Constant, recording (GAV, field reference, field value).

//            System.out.println("** FIELDS");
//            annotatedFields.forEach(
//                    s -> s.forEach(
//                            System.out::println
//                    )
//            );
    }

}

// (rm -rf target ; mvn install && cd target/it/simple-it/ && mvn vivid.cherimoya:cherimoya-maven-plugin:1.0:verify)

// tests:
// no pom declarations of version -> skip
// 1 pom declarations of version, which = current version -> skip
// 1 pom declaration of version, different from current pom version -> process
// 1 pom decl, unresolvable -> error
// 2 pom decls, 1 unresolvable -> error

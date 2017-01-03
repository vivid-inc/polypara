package vivid.cherimoya;

/*
 * Copyright (C) 2017 The Cherimoya Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.i18n.I18N;

import java.util.*;

/**
 * @since 1.0
 */
@Mojo(
        name = "verify-constants",
        defaultPhase = LifecyclePhase.PROCESS_CLASSES
)
public class VerifyConstantsMojo
        extends AbstractMojo
{

    private static final String I18N_RESOURCE_BUNDLE = "cherimoya-i18n";

    /**
     * Flag to easily skip execution.
     *
     * @since 1.0
     */
    @Parameter(defaultValue = "false")
    private boolean skip;

    @Component
    private I18N i18n;

    /**
     * The Maven Project Object
     */
    @Parameter( defaultValue = "${project}", readonly = true, required = true )
    private MavenProject project;

    /**
     * Expect these artifact versions to be available Process artifacts of these versions.
     * An error will be generated if a given artifact version is not available in the local repository.
     *
     * @since 1.0
     */
    @Parameter
    private String[] includeVersions; // TODO

    public void execute()
            throws MojoExecutionException
    {
        if (skip) {
            info("vivid.cherimoya.actions.skipping-execution-via-configuration");
            return;
        }

        final String g_a = String.format(
                "%s:%s",
                project.getModel().getGroupId(),
                project.getModel().getArtifactId()
        );

        final Set<String> versions = allVersions();
        if (versions.size() == 1) {
            warn(
                    "vivid.cherimoya.warning.cw-1-skipping-execution-via-singular-version",
                    versions.iterator().next(),
                    g_a
            );
            return;
        }

        // TODO scan for annotated items in all classes in current project (the generated classes) + all versions available in the repo.
        // TODO process each annotated item: (full qualified class and field name, value as java primitive type)
        // TODO Scan classes in each version's JARs.
        // https://stackoverflow.com/questions/11341783/accessing-classes-in-custom-maven-reporting-plugin

        // TODO Compute and report.
    }

    private Set<String> allVersions() {
        final Set<String> versions = new TreeSet<>();

        // Recognize this current version
        versions.add(project.getModel().getVersion());

        // TODO Identify all available versions of this G:A, known to the running Maven system. Does this mean the available versions in the localRepository and the remoteRepository's? artifactMetadataSource.retrieveAvailableVersions(artifact, localRepository, Collections.<ArtifactRepository>emptyList());

        return versions;
    }

    private String getText(
            final String i18nKey,
            final Object... args
    ) {
        final Locale locale = Locale.getDefault();
        return i18n.format(I18N_RESOURCE_BUNDLE, locale, i18nKey, args);
    }

    private void info(
            final String i18nKey,
            final Object... args
    ) {
        getLog().info(getText(i18nKey, args));
    }

    private void warn(
            final String i18nKey,
            final Object... args
    ) {
        getLog().warn(getText(i18nKey, args));
    }

}

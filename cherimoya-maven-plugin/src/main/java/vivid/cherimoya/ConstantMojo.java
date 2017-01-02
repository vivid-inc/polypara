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

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.codehaus.plexus.i18n.I18N;

import java.util.Locale;

/**
 * TODO
 * scan for annotated items in all classes in current project + all specified versions in .m2 repo
 * process each annotated item: (full qualified class and field name, value as java primitive type)
 *
 * ignores field visibility
 * pom: specify target versions: https://maven.apache.org/components/enforcer/enforcer-rules/versionRanges.html
 *
 * @since 1.0
 */
@Mojo(
        name = "verify-constants",
        defaultPhase = LifecyclePhase.PROCESS_CLASSES
)
public class ConstantMojo
        extends AbstractMojo
{
    /**
     * Flag to easily skip execution.
     *
     * @parameter property="verify-constants.skip" default-value="false"
     *
     * @since 1.0
     */
    private boolean skip;

    /**
     * @component
     *
     * @since 1.0
     */
    private I18N i18n;

    public void execute()
            throws MojoExecutionException
    {
        final Locale locale = Locale.getDefault();

        if (skip) {
            getLog().info(i18n.format("vivid.cherimoya.actions.skipping-execution", locale));
            return;
        }

getLog().info("hello from cherimoya");

        // TODO Get the GAV of the current project -> gather all available versions of G&A from repo.
        // TODO Scan the generated classes.
        // TODO Scan classes in each version's JARs.
        // TODO Compute and report.
    }

}

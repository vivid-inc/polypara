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
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.i18n.I18N;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

/**
 * TODO
 * scan for annotated items in all classes in current project + all specified versions in .m2 repo
 * process each annotated item: (full qualified class and field name, value as java primitive type)
 *
 * ignores field visibility
 * pom: specify target versions: https://maven.apache.org/components/enforcer/enforcer-rules/versionRanges.html
 */
@Mojo( name = "constant", defaultPhase = LifecyclePhase.PROCESS_SOURCES )
public class ConstantMojo
        extends AbstractMojo
{
    /**
     * Flag to easily skip execution.
     *
     * @parameter property="cherimoya.skip" default-value="false"
     */
    protected boolean skip;

    /**
     * @component
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





        // TODO Get the GAV of the current project -> gather all available versions of G&A from repo.
        // TODO Scan the generated classes.
        // TODO Scan classes in each version's JARs.
        // TODO Compute and report.

        File f = outputDirectory;

        if ( !f.exists() )
        {
            f.mkdirs();
        }

        File touch = new File( f, "touch.txt" );

        FileWriter w = null;
        try
        {
            w = new FileWriter( touch );

            w.write( "touch.txt" );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Error creating file " + touch, e );
        }
        finally
        {
            if ( w != null )
            {
                try
                {
                    w.close();
                }
                catch ( IOException e )
                {
                    // ignore
                }
            }
        }
    }

    /**
     * Location of the file.
     */
    @Parameter( defaultValue = "${project.build.directory}", property = "outputDir", required = true )
    private File outputDirectory;

}

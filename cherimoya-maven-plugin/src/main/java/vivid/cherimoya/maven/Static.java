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

import org.apache.maven.project.MavenProject;
import vivid.cherimoya.annotation.Constant;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @since 1.0
 */
public class Static {

    @Constant
    public static final String POM_CHERIMOYA_VERIFY_MOJO_NAME = "verify";

    @Constant
    static final String POM_CHERIMOYA_CONSTANTS_SKIP_PROPERTY_KEY = "cherimoya.constant.skip";

    private Static() {
        // Cannot be instantiated.
    }

    static URLClassLoader classLoaderForDirectory(
            final String dir
    ) throws MalformedURLException {
        return URLClassLoader.newInstance(
                new URL[] {
                        new File(dir).toURI().toURL()
                }
        );
    }

    static String mavenGAOf(
            final MavenProject mavenProject
    ) {
        return String.format(
                "%s:%s",
                mavenProject.getModel().getGroupId(),
                mavenProject.getModel().getArtifactId()
        );
    }

}

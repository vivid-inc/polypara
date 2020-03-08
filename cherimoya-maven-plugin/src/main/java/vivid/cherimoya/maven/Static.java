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
import io.vavr.collection.SortedSet;
import io.vavr.collection.Stream;
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
    static final String MAVEN_VERSION_RANGE_ENTIRE_RANGE = "(,)";

    @Constant
    static final String POM_CHERIMOYA_CONSTANTS_SKIP_PROPERTY_KEY = "cherimoya.constant.skip";

    @Constant
    public static final String POM_CHERIMOYA_VERIFY_MOJO_NAME = "verify";

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

    /**
     * Does set {@code a} contain all elements of set {@code b}?
     */
    static <T> boolean containsAll(
            final Set<T> a, final Set<T> b
    ) {
        return b.diff(a).isEmpty();
    }

    static String listOfVersions(
            final SortedSet<String> versions
    ) {
        return Stream.ofAll(versions)
                .intersperse("  ")
                .fold("", String::concat);
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

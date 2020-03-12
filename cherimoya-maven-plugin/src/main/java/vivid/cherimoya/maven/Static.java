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

import io.vavr.collection.List;
import io.vavr.collection.Stream;
import org.apache.maven.project.MavenProject;
import vivid.cherimoya.annotation.Constant;

class Static {

    static final String POM_CHERIMOYA_CONSTANTS_SKIP_PROPERTY_KEY = "cherimoya.constant.skip";

    @Constant
    static final String POM_CHERIMOYA_VERIFY_MOJO_NAME = "verify";

    private Static() {
        // Cannot be instantiated.
    }

    /**
     * @return human-readable string representation of a set of version strings
     */
    static String listOfVersions(
            final List<String> versions
    ) {
        return Stream.ofAll(versions)
                .intersperse("  ")
                .fold("", String::concat);
    }

    /**
     * @return string representation of the group and artifact parts of a Maven GAV
     */
    static String mavenGAOf(
            final MavenProject mavenProject
    ) {
        return String.format(
                "%s:%s",
                mavenProject.getModel().getGroupId(),
                mavenProject.getModel().getArtifactId()
        );
    }

    /**
     * @return string representation of the group and artifact parts of a Maven GAV
     */
    static String mavenGAVOf(
            final String groupId,
            final String artifactId,
            final String version
    ) {
        return String.format(
                "%s:%s:%s",
                groupId,
                artifactId,
                version
        );
    }

}

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

import io.vavr.collection.List;
import io.vavr.collection.Stream;
import org.apache.maven.project.MavenProject;
import vivid.cherimoya.annotation.Constant;

import java.io.File;
import java.util.Enumeration;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

class Static {

    private static final String CONSTANT_REASON =
            "Users prefer their existing Maven POM Cherimoya configuration to " +
            "remain compatible as-is with newer versions of this plugin.";

    @Constant(rationale = CONSTANT_REASON)
    static final String POM_CHERIMOYA_REPORTING_LEVEL_CONFIGURATION_KEY = "reportingLevel";

    @Constant(rationale = CONSTANT_REASON)
    static final String POM_CHERIMOYA_SKIP_PROPERTY_KEY = "cherimoya.skip";

    @Constant(rationale = CONSTANT_REASON)
    static final String POM_CHERIMOYA_VERIFY_MOJO_NAME = "verify";

    @Constant(rationale = CONSTANT_REASON)
    static final String POM_CHERIMOYA_VERSIONS_CONFIGURATION_KEY = "versions";


    private Static() {
        // Cannot be instantiated.
    }


    /**
     * From https://stackoverflow.com/questions/33242577/how-do-i-turn-a-java-enumeration-into-a-stream
     */
    static <T> java.util.stream.Stream<T> enumerationAsStream(
            final Enumeration<T> e
    ) {
        return StreamSupport.stream(
                new Spliterators.AbstractSpliterator<T>(Long.MAX_VALUE, Spliterator.ORDERED) {
                    public boolean tryAdvance(final Consumer<? super T> action) {
                        if (e.hasMoreElements()) {
                            action.accept(e.nextElement());
                            return true;
                        }
                        return false;
                    }
                    @Override
                    public void forEachRemaining(final Consumer<? super T> action) {
                        while (e.hasMoreElements()) {
                            action.accept(e.nextElement());
                        }
                    }
                },
                false);
    }

    /**
     * @return fully-qualified field name, suitable for pasting directly into an IDE's navigation facility
     */
    static String fieldFullyQualifiedName(
            final String clazzName,
            final String fieldName
    ) {
        return String.format(
                "%s.%s",
                clazzName,
                fieldName
        );
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

    /**
     * @return absolute path of a Java Jar file and a specific entry within it
     */
    static String pathInJarFile(
            final File jarFile,
            final String entryName
    ) {
        return String.format(
                "%s(%s)",
                jarFile.getAbsolutePath(),
                entryName
        );
    }

}

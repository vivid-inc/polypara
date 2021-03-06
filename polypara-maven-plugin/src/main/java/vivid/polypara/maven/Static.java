/*
 * Copyright 2017 The Polypara Authors
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

package vivid.polypara.maven;

import io.vavr.collection.List;
import io.vavr.collection.Stream;
import org.apache.maven.project.MavenProject;
import vivid.polypara.annotation.Constant;

import java.io.File;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

class Static {

    private static final String DONT_MAKE_ME_THINK =
            "Users prefer their existing Maven POM Polypara configuration to " +
                    "remain compatible as-is with newer versions of this Polypara Maven plugin.";

    @Constant(rationale = DONT_MAKE_ME_THINK)
    static final String POM_POLYPARA_REPORTING_LEVEL_CONFIGURATION_KEY = "reportingLevel";

    @Constant(rationale = DONT_MAKE_ME_THINK)
    static final String POM_POLYPARA_SKIP_PROPERTY_KEY = "vivid.polypara.skip";

    @Constant(rationale = DONT_MAKE_ME_THINK)
    static final String POM_POLYPARA_VERIFY_MOJO_NAME = "verify";

    @Constant(rationale = DONT_MAKE_ME_THINK)
    static final String POM_POLYPARA_VERSIONS_CONFIGURATION_KEY = "versions";


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
        Objects.requireNonNull(clazzName, "clazzName is null");
        Objects.requireNonNull(fieldName, "fieldName is null");
        return String.format(
                "%s.%s",
                clazzName,
                fieldName
        );
    }

    static String humanReadableVersionList(
            final List<String> versions
    ) {
        Objects.requireNonNull(versions, "versions is null");
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
        Objects.requireNonNull(mavenProject, "mavenProject is null");
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
        Objects.requireNonNull(groupId, "groupId is null");
        Objects.requireNonNull(artifactId, "artifactId is null");
        Objects.requireNonNull(version, "version is null");
        return String.format(
                "%s:%s:%s",
                groupId,
                artifactId,
                version
        );
    }

    private static final String ABBREVIATION_SUFFIX = "...";

    static String objectValueAsAbbreviatedString(
            final Object obj,
            final int maximumStringLength
    ) {
        final String str = String.valueOf(obj);
        if (str.length() <= Math.max(ABBREVIATION_SUFFIX.length(), maximumStringLength)) {
            return str;
        }

        return String.format(
                "%s%s",
                str.substring(0, maximumStringLength - ABBREVIATION_SUFFIX.length()),
                ABBREVIATION_SUFFIX
        );
    }

    /**
     * @return absolute path of a Java Jar file and a specific entry \
     *   within it in the same format produced by maven-compiler-plugin
     */
    static String pathInJarFile(
            final File jarFile,
            final String entryName
    ) {
        Objects.requireNonNull(jarFile, "jarFile is null");
        Objects.requireNonNull(entryName, "entryName is null");
        return String.format(
                "%s(%s)",
                jarFile.getAbsolutePath(),
                entryName
        );
    }

}

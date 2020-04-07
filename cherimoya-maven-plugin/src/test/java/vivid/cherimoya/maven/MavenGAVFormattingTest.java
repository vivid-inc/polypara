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
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import vivid.junit5.params.provider.StreamableStaticFieldSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MavenGAVFormattingTest {

    @Test
    void gaNullParameter() {
        assertThrows(
                NullPointerException.class,
                () -> Static.mavenGAOf(null)
        );
    }

    private static final MavenProject obstensiblyCorrectlyFormedMavenProject;
    static {
        obstensiblyCorrectlyFormedMavenProject = new MavenProject();
        obstensiblyCorrectlyFormedMavenProject.setGroupId("com.sun");
        obstensiblyCorrectlyFormedMavenProject.setArtifactId("java");
    }

    @Test
    void gaFromMavenProject() {
        assertEquals(
                "com.sun:java",
                Static.mavenGAOf(obstensiblyCorrectlyFormedMavenProject)
        );
    }

    public static final List<Arguments> nullParameters =
            List.of(
                    Arguments.of(null, "xyz", "1.2.3"),
                    Arguments.of("a.b.c", null, "1.2.3"),
                    Arguments.of("a.b.c", "xyz", null)
            );

    @ParameterizedTest
    @StreamableStaticFieldSource("nullParameters")
    void gavNullParameters(
            final String groupId,
            final String artifactId,
            final String version
    ) {
        assertThrows(
                NullPointerException.class,
                () -> Static.mavenGAVOf(
                        groupId,
                        artifactId,
                        version
                )
        );
    }

    @Test
    void gavFromLooseParameters() {
        assertEquals(
                "org.kernel:linux-kernel:2.4.35pl6ac7rc8-beta9",
                Static.mavenGAVOf(
                        "org.kernel",
                        "linux-kernel",
                        "2.4.35pl6ac7rc8-beta9"
                )
        );
    }

}

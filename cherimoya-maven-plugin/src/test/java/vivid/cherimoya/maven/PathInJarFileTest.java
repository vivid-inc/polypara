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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import vivid.junit5.params.provider.StreamableStaticFieldSource;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PathInJarFileTest {

    private static final String JAR_FILE_PATH = "/home/me/.m2/repository/scheduler.jar";
    private static final String DESCRIPTOR_JAR_FILE_ENTRY_PATH = "/descriptor.json";

    public static final List<Arguments> nullParameters =
            List.of(
                    Arguments.of(null, "/descriptor.json"),
                    Arguments.of(new File("scheduler.jar"), null)
            );

    @ParameterizedTest
    @StreamableStaticFieldSource("nullParameters")
    void nullParameters(
            final File jarFile,
            final String entryName
    ) {
        assertThrows(
                NullPointerException.class,
                () -> Static.pathInJarFile(jarFile, entryName)
        );
    }

    @Test
    void plain() {
        assertEquals(
                "/home/me/.m2/repository/scheduler.jar(/descriptor.json)",
                Static.pathInJarFile(
                        new File(JAR_FILE_PATH),
                        DESCRIPTOR_JAR_FILE_ENTRY_PATH
                )
        );
    }

}

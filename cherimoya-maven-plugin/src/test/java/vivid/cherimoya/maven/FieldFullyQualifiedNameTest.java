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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import vivid.cherimoya.maven.testing.StaticFieldSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FieldFullyQualifiedNameTest {

    public static final Stream<Arguments> nullParameters =
            Stream.of(
                    Arguments.of(null, "abc"),
                    Arguments.of("abc", null)
            );

    @ParameterizedTest
    @StaticFieldSource("nullParameters")
    void nullParameters(
            final String clazzName,
            final String fieldName
    ) {
        assertThrows(
                NullPointerException.class,
                () -> Static.fieldFullyQualifiedName(clazzName, fieldName)
        );
    }

    public static final Stream<Arguments> parameters =
            Stream.of(
                    Arguments.of("", "abc", ".abc"),
                    Arguments.of("abc", "", "abc."),
                    Arguments.of("org.oss.Classy", "nicety", "org.oss.Classy.nicety")
            );

    @ParameterizedTest
    @StaticFieldSource("parameters")
    void variousParameters(
            final String clazzName,
            final String fieldName,
            final String expected
    ) {
        assertEquals(
                expected,
                Static.fieldFullyQualifiedName(clazzName, fieldName)
        );
    }

}

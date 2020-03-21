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
import vivid.cherimoya.junit5.params.provider.StreamableStaticFieldSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HumanReadableListOfVersionsTest {

    @Test
    void nullParameter() {
        assertThrows(
                NullPointerException.class,
                () -> Static.humanReadableVersionList(null)
        );
    }

    public static final List<Arguments> versions =
            List.of(
                    Arguments.of(List.empty(), ""),
                    Arguments.of(List.of("1"), "1"),
                    Arguments.of(List.of("1", "2"), "1  2"),
                    Arguments.of(List.of("1.0", "2.0", "3.0"), "1.0  2.0  3.0")
            );

    @ParameterizedTest
    @StreamableStaticFieldSource("versions")
    void variousParameters(
            final List<String> versions,
            final String expected
    ) {
        assertEquals(
                expected,
                Static.humanReadableVersionList(versions)
        );
    }

}

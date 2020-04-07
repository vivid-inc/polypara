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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import vivid.junit5.params.provider.StreamableStaticFieldSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ObjectValueAbbreviationTest {

    public static final List<Arguments> objects =
            List.of(
                    // Instances of null
                    Arguments.of(null, 20, "null"),

                    // Instances of byte
                    Arguments.of((byte)0x7A, 20, "122"),

                    // Instances of int
                    Arguments.of(123, 20, "123"),
                    Arguments.of(-1, 20, "-1"),
                    Arguments.of(0x7fffffff, 20, "2147483647"),

                    // Instances of boolean
                    Arguments.of(true, 4, "true"),
                    Arguments.of(false, 4, "f..."),

                    // Instances of type java.lang.String
                    Arguments.of("Happy clouds", 5, "Ha..."),
                    Arguments.of(
                            "Everything I touch\nwith tenderness, alas,\n" +
                                    "pricks like a bramble. - Kobayashi Issa",
                            20,
                            "Everything I touc..."
                    ),

                    // Instances of all other types
                    Arguments.of(
                            new Object(){
                                @Override
                                public String toString() {
                                    return "The lurking suspicion that something could be simplified is the " +
                                            "world's richest source of rewarding challenges. - Edsgar Dijkstra";
                                }
                            },
                            20,
                            "The lurking suspi..."
                    )
            );

    @ParameterizedTest
    @StreamableStaticFieldSource("objects")
    void abbreviation(
            final Object obj,
            final int length,
            final String expected
    ) {
        assertEquals(
                expected,
                Static.objectValueAsAbbreviatedString(
                        obj,
                        length
                )
        );
    }

}

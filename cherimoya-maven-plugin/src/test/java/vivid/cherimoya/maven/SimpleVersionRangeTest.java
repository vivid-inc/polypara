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

import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.control.Option;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import vivid.cherimoya.junit5.params.provider.StreamableStaticFieldSource;
import vivid.cherimoya.maven.testing.Algorithms;

import java.util.function.BiFunction;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SimpleVersionRangeTest {

    public static final List<Arguments> nullParameters =
            List.of(
                    Arguments.of(null, Option.none()),
                    Arguments.of("1", null)
            );

    @ParameterizedTest
    @StreamableStaticFieldSource("nullParameters")
    void nullParameters(
            final String start,
            final Option<String> end
    ) {
        assertThrows(
                NullPointerException.class,
                () -> new SimpleVersionRange(start, end)
        );
    }

    /**
     * Requirements: Versions are listed in ascending order. No two entries are equal.
     */
    public static final List<Arguments> versions =
            List.of(
                    Arguments.of("0",           Option.none(),              "0"),
                    Arguments.of("1.0",         Option.none(),              "1.0"),
                    Arguments.of("1.2.3",       Option.of("4.5.6-alpha-7"), "1.2.3 ~ 4.5.6-alpha-7"),
                    Arguments.of("1.3",         Option.none(),              "1.3"),
                    Arguments.of("2.0-alpha-1", Option.none(),              "2.0-alpha-1"),
                    Arguments.of("2.0-beta-3",  Option.none(),              "2.0-beta-3"),
                    Arguments.of("2.0-rc4",     Option.none(),              "2.0-rc4"),
                    Arguments.of("2.0",         Option.none(),              "2.0"),
                    Arguments.of("2.0.1-rc-1",  Option.none(),              "2.0.1-rc-1"),
                    Arguments.of("2.0.1",       Option.none(),              "2.0.1"),
                    Arguments.of("2.1.0",       Option.none(),              "2.1.0")
            );

    @ParameterizedTest
    @StreamableStaticFieldSource("versions")
    void stringRepresentation(
            final String start,
            final Option<String> end,
            final String expected
    ) {
        final SimpleVersionRange simpleVersionRange = new SimpleVersionRange(
                start,
                end
        );
        assertEquals(
                expected,
                simpleVersionRange.toString()
        );
    }

    public static final List<Arguments> versionsPairwise = Algorithms.forwardPairwise(
            versions.map(args -> (String) args.get()[0]),
            v -> new SimpleVersionRange(v, Option.none())
    )
            .map(e -> Arguments.of(e._1, e._2));

    @ParameterizedTest
    @StreamableStaticFieldSource("versionsPairwise")
    void compareEarlier(
            final SimpleVersionRange earlier,
            final SimpleVersionRange later
    ) {
        assertTrue(
                earlier.compareTo(later) < 0
        );
    }

    @ParameterizedTest
    @StreamableStaticFieldSource("versionsPairwise")
    void compareLater(
            final SimpleVersionRange earlier,
            final SimpleVersionRange later
    ) {
        assertTrue(
                later.compareTo(earlier) > 0
        );
    }

    static final BiFunction<Tuple2<SimpleVersionRange, Integer>, Tuple2<SimpleVersionRange, Integer>, Arguments> eqPair =
            (a, b) -> Arguments.of(a._1, b._1, (int) a._2 == b._2);
    static final Function<List<Tuple2<SimpleVersionRange, Integer>>, List<Arguments>> eqPairOfVersionCombinations =
            list ->
                    list.flatMap(j ->
                            list.map(k -> eqPair.apply(j, k))
                    );
    public final static List<Arguments> versionCombinations = versions
            .map(args -> (String) args.get()[0])
            .map(v -> new SimpleVersionRange(v, Option.none()))
            .zipWithIndex()
            .transform(eqPairOfVersionCombinations);

    @ParameterizedTest
    @StreamableStaticFieldSource("versionCombinations")
    void equality(
            final SimpleVersionRange a,
            final SimpleVersionRange b,
            final boolean expectedEqual
    ) {
        assertEquals(
                expectedEqual,
                a.equals(b)
        );
    }

}

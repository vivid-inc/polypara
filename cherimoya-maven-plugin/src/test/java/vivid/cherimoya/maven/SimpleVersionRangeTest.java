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
import vivid.cherimoya.maven.testing.StaticFieldSource;

import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SimpleVersionRangeTest {

    private static final List<Arguments> versions =
            List.of(
                    Arguments.of("1.0", Option.none(), "1.0"),
                    Arguments.of("1.2.3", Option.of("4.5.6-alpha-7"), "1.2.3 ~ 4.5.6-alpha-7"),
                    Arguments.of("1.3", Option.none(), "1.3"),
                    Arguments.of("2.0-alpha-1", Option.none(), "2.0-alpha-1"),
                    Arguments.of("2.0-beta-3", Option.none(), "2.0-beta-3"),
                    Arguments.of("2.0-rc4", Option.none(), "2.0-rc4"),
                    Arguments.of("2.0", Option.none(), "2.0"),
                    Arguments.of("2.0.1-rc-1", Option.none(), "2.0.1-rc-1"),
                    Arguments.of("2.0.1", Option.none(), "2.0.1"),
                    Arguments.of("2.1.0", Option.none(), "2.1.0")
            );
    public static final Stream<Arguments> versionsStream = versions.toJavaStream();

    @ParameterizedTest
    @StaticFieldSource("versionsStream")
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

    /**
     * @return a list of pairs in geometric progression over the list of elements,
     *   meaning no pair._1 appears later in the source list than pair._2
     */
    private static <T, U> List<Tuple2<U, U>> pairwise(List<T> list, Function<T, U> factory) {
        T first = list.get(0);
        List<T> rest = list.drop(1);
        if (rest.isEmpty()) {
            return List.empty();
        }

        return rest
                // Generate pairs of (first, each remaining element in list)
                .map(x -> new Tuple2<U, U>(factory.apply(first), factory.apply(x)))

                // Recurse down the list
                .appendAll(pairwise(rest, factory));
    }

    public static final List<Arguments> versionPairs = pairwise(
            List.ofAll(versions).map(a -> (String) a.get()[0]),
            v -> new SimpleVersionRange(v, Option.none())
    )
            .map(e -> Arguments.of(e._1, e._2));

    public static final Stream<Arguments> versionsPairwise = versionPairs.toJavaStream();

    @ParameterizedTest
    @StaticFieldSource("versionsPairwise")
    void comparator(
            final SimpleVersionRange earlier,
            final SimpleVersionRange later
    ) {
        assertTrue(
                earlier.compareTo(later) < 0
        );
    }

}

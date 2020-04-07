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

package vivid.polypara.maven.testing;

import io.vavr.Tuple2;
import io.vavr.collection.List;

import java.util.function.Function;

public class Algorithms {

    /**
     * @return a list of tuples taken in progression over the list of elements,
     *   where in a given tuple _1 always occurs earlier than _2.
     */
    public static <T, U> List<Tuple2<U, U>> forwardPairwise(
            final List<T> list,
            final Function<T, U> toOutputType
    ) {
        if (list.size() < 2) {
            return List.empty();
        }

        final T first = list.get(0);
        final List<T> rest = list.drop(1);

        return rest
                // Generate pairs of (first, each remaining element in list)
                .map(x -> new Tuple2<U, U>(toOutputType.apply(first), toOutputType.apply(x)))

                // Recurse down the list
                .appendAll(forwardPairwise(rest, toOutputType));
    }

}

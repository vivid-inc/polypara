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

package vivid.cherimoya.junit5.params.provider;

import org.junit.jupiter.params.provider.ArgumentsSource;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@code StreamableStaticFieldSource} is an {@link ArgumentsSource} which provides
 * access to a {@code Stream} of {@code Arguments} supplied by a {@code public static}
 * field. The field's value produces a {@code Stream} by being called with
 * either the {@code stream()} method common to Java's collection classes or the
 * {@code toJavaStream()} method common to VAVRs collection classes.
 *
 * <pre>
 * <code>
 *     public static final List<Arguments> testParameters =
 *             List.of(
 *                     Arguments.of(null, 123),
 *                     Arguments.of("abc", 789)
 *             );
 *
 *     @ParameterizedTest
 *     @StreamableStaticFieldSource("testParameters")
 *     void myTest(
 *             final String m,
 *             final int n
 *     ) {
 *         ...
 *     }
 * </code>
 * </pre>
 *
 * @since 0.3.0
 * @see org.junit.jupiter.params.provider.ArgumentsSource
 * @see org.junit.jupiter.params.ParameterizedTest
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ArgumentsSource(StreamableStaticFieldArgumentsProvider.class)
public @interface StreamableStaticFieldSource {

    /*
     * Name of the field from whose value arguments are sourced.
     */
    String value();

}

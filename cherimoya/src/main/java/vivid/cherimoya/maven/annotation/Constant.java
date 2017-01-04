/*
 * Copyright (C) 2017 The Cherimoya Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package vivid.cherimoya.maven.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Signifies that the annotated element is expected to bear the same value since inception and throughout
 * ensuing releases.
 * Use when considering a class field and you think to yourself: "The value of this field cannot change, even in successive versions."
 *
 * A qualifying element has the same fully qualified class name identifier and field identifier. As an example:
 *
 * <pre>
 * package summer.breeze;
 *
 * class Yacht {
 *     {@literal @}Constant
 *     static final int LENGTH_MM = 9700;
 *     ...
 * }
 * </pre>
 *
 * <p>The {@code LENGTH} field is marked as constant, signifying that the fully-qualified field
 * {@code summer.breeze.Yacht.LENGTH} is expected to constantly have the same value integer value of {@code 2370}
 * contiguously in all available packaged versions from the field's first appearance throughout its last.
 * The field is also expected to be annotated as {@code @Constant} in all relevant versions.</p>
 *
 * <p>The field value must be a Java primitive.</p>
 *
 * @since 1.0
 */
/*
 * Design Decisions
 * ----
 *
 * An annotation is used because the annotation is stored in the Java .class file and is machine readable even after the
 * artifact is published. Alternatively, using a keyword in a comment is susceptible to mispelling that won't be caught
 * by the build, and the comment is not preserved in the .class file.
 */
@Retention(RetentionPolicy.CLASS)
@Target({
        ElementType.FIELD
})
public @interface Constant {}

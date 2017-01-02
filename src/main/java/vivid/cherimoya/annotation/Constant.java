/*
 * Copyright (C) 2010 The Cherimoya Authors
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

package vivid.cherimoya.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Signifies that the annotated element is expected to bear the same value since inception and throughout
 * ensuing releases.
 * A qualifying element has the same fully qualified class name identifier and field identifier. As a running example:
 *
 * <pre>
 * package summer.breeze;
 *
 * class Yacht {
 *     {@literal @}Constant
 *     static final LENGTH = 2370;
 *     ...
 * }
 * </pre>
 *
 * <p>The {@code LENGTH} field is marked as constant, signifying that the fully-qualified field
 * {@code summer.breeze.Yacht.LENGTH} is expected to constantly have the same value integer value of {@code 2370}
 * contiguously in all available packaged versions from the field's first appearance throughout its last.
 * The field is also expected to be annotated as {@code @Constant} in all relevant versions.</p>
 *
 * @since 1.0.0
 */
@Retention(RetentionPolicy.CLASS)
@Target({
        ElementType.FIELD
})
public @interface Constant {}

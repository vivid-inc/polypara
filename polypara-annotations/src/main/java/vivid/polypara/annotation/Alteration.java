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

package vivid.polypara.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Record a change to an otherwise {@code @Constant} field.
 *
 * Polypara will take alterations into account, forgiving only those that transgress the vow to constancy
 * by means of clear indication of change using this record of alteration.
 *
 * The reasoning behind this is plural:
 * A) The field value aught to be held constant over successive releases, and the use of Polypara
 *    reinforces this notion. The need for constancy may be because there are multiple actors that
 *    depend on a given value staying constant, outstanding promises, or for reliable interfacing
 *    or interchange.
 * B) Any accidental change will be caught by Polypara during the Maven build cycle. However, when
 *    a change is indeed intentional, Polypara accomodates the change. But that accomodation
 *    must be explicitly signaled by the developer to Polypara and brought to the attention of
 *    other participants.
 *
 * In this way, Polypara can bring more controlled rigour and correctness to your development process.
 *
 * This record of intentional change can be particular effective for focusing discussion during code reviews.
 *
 * @since 1.1.0
 */
@Retention(RetentionPolicy.CLASS)
@Target({
        ElementType.FIELD
})
public @interface Alteration {

    /**
     * The version in which the field was subjected to change, compared to its immediate prior version.
     */
    String version();

    /**
     * Required, to explain the necessity of the change. Provides an avenue to build awareness of
     * potential consequences.
     */
    String[] explanation();

    boolean valueChanged() default false;

    String fromFQN() default "";

}

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

import io.vavr.Tuple2;
import io.vavr.collection.List;

import java.io.Closeable;

/**
 * Persistence tier contract for the storage of {@code Constant} field information and the computation of
 * detailed value constancy violations.
 */
interface ConstantsData
        extends Closeable {

    void recordConstantFields(
            final String version,
            final List<Tuple2<String, Object>> fields
    );

    List<ConstancyViolation> constancyViolationDescriptions();

    int constantFieldsCount();

}

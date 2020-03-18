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

import java.util.Objects;

class JavaClasses {

    private static String JAVA_CLASS_FILENAME_SUFFIX = ".class";

    private static String JAVA_CLASS_FILE_MAGIC_HEADER = "cafebabe";

    private JavaClasses() {
        // Hide the public constructor
    }

    static boolean isJavaClassFilename(
            final String filename
    ) {
        return filename.endsWith(JAVA_CLASS_FILENAME_SUFFIX);
    }

    static boolean hasJavaClassFileMagic(
            final byte[] bytes
    ) {
        Objects.requireNonNull(bytes, "bytes is null");
        if (bytes.length < 4) {
            return false;
        }
        final String magic = String.format(
                "%02X%02X%02X%02X",
                bytes[0], bytes[1], bytes[2], bytes[3]
        );
        return JAVA_CLASS_FILE_MAGIC_HEADER.equalsIgnoreCase(magic);
    }

}

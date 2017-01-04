/**
 * Copyright 2017 The Cherimoya Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package vivid.cherimoya.maven;

import org.objectweb.asm.Type;
import vivid.cherimoya.annotation.Constant;

import java.io.File;

/**
 * @since 1.0
 */
class Static {

    private static final String CONSTANT_DESCRIPTOR = Type.getType(Constant.class).getDescriptor();

    private static final String JAVA_CLASS_FILE_EXTENSION = "class";

    private Static() {
        // Cannot be instantiated.
    }

    /**
     * @return the fully-qualified name of the field, prefixed by the fully-qualified name of the class it is a member of
     */
    static String fullyQualifiedFieldName(final String clazzName, final String fieldName) {
        return String.format("%s.%s", clazzName, fieldName);
    }

    static boolean isConstantAnnotation(final String fieldTypeDescriptor) {
        return CONSTANT_DESCRIPTOR.equals(fieldTypeDescriptor);
    }

    static boolean isJavaClassFile(final File file) {
        return file.isFile() && file.getName().endsWith("." + JAVA_CLASS_FILE_EXTENSION);
    }

}

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

import io.vavr.collection.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import vivid.cherimoya.maven.testing.StaticFieldSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JavaClassesTest {

    public static final List<Arguments> filenames =
            List.of(
                    Arguments.of("", false),
                    Arguments.of(".clas", false),
                    Arguments.of(".class", true),
                    Arguments.of(".classs", false),
                    Arguments.of(".clazz", false),
                    Arguments.of("a", false),
                    Arguments.of("a.", false),
                    Arguments.of("a.clas", false),
                    Arguments.of("a.class", true),
                    Arguments.of("a.classs", false),
                    Arguments.of("a.clazz", false)
            );

    @ParameterizedTest
    @StaticFieldSource("filenames")
    void classFileName(
            final String filename,
            final boolean expected
    ) {
        assertEquals(
                expected,
                JavaClasses.isJavaClassFilename(filename)
        );
    }

    public static final List<Arguments> classFileBytes =
            List.of(
                    Arguments.of(new byte[] {}, false),

                    Arguments.of(new byte[] {(byte)0x12}, false),
                    Arguments.of(new byte[] {(byte)0x12, (byte)0x34}, false),
                    Arguments.of(new byte[] {(byte)0x12, (byte)0x34, (byte)0x56}, false),
                    Arguments.of(new byte[] {(byte)0x12, (byte)0x34, (byte)0x56, (byte)0x78}, false),
                    Arguments.of(new byte[] {(byte)0x12, (byte)0x34, (byte)0x56, (byte)0x78, (byte)0x9A}, false),

                    Arguments.of(new byte[] {(byte)0xCA}, false),
                    Arguments.of(new byte[] {(byte)0xCA, (byte)0xFE}, false),
                    Arguments.of(new byte[] {(byte)0xCA, (byte)0xFE, (byte)0xBA}, false),
                    Arguments.of(new byte[] {(byte)0xCA, (byte)0xFE, (byte)0xBA, (byte)0xBE}, true),
                    Arguments.of(new byte[] {(byte)0xCA, (byte)0xFE, (byte)0xBA, (byte)0xBE, (byte)0x00}, true)
            );

    @ParameterizedTest
    @StaticFieldSource("classFileBytes")
    void classFileBytes(
            final byte[] inputStreamBytes,
            final boolean expected
    ) {
        assertEquals(
                expected,
                JavaClasses.hasJavaClassFileMagic(inputStreamBytes)
        );
    }

    @Test
    void nullClassFileBytes() {
        assertThrows(
                NullPointerException.class,
                () -> JavaClasses.hasJavaClassFileMagic(null)
        );
    }

}

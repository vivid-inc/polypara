/*
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

import io.vavr.control.Option;
import org.objectweb.asm.ClassReader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

class AsmClassReaders {

    private static String JAVA_CLASS_FILENAME_SUFFIX = ".class";

    private static String JAVA_CLASS_FILE_MAGIC_HEADER = "cafebabe";

    private AsmClassReaders() {
        // Hide the public constructor
    }

    private static boolean isJavaClassFilename(
            final String filename
    ) {
        return filename.endsWith(JAVA_CLASS_FILENAME_SUFFIX);
    }

    /**
     * @return a usable tuple of Java class file data iff the args indicate a Java class file
     */
    private static Option<ClassReader> javaClassFileData(
            final Mojo mojo,
            final InputStream inputStream,
            final String filename
    ) throws IOException {
        // Does the file header indicate Java .class file magic?
            final BufferedInputStream bis = new BufferedInputStream(inputStream);
            bis.mark(4);
            final String magic = String.format(
                    "%02X%02X%02X%02X",
                    bis.read(),
                    bis.read(),
                    bis.read(),
                    bis.read()
            );
            bis.reset();

            if (JAVA_CLASS_FILE_MAGIC_HEADER.equalsIgnoreCase(magic)) {
                return Option.of(new ClassReader(bis));
            }

            mojo.getLog().debug(
                    "Ignoring re Java .class file header magic: " +
                            filename
            );
            return Option.none();
    }

    private static Option<ClassReader> classReaderOf(
            final Mojo mojo,
            final InputStream inputStream,
            final String filename
    ) throws IOException {
        if (!isJavaClassFilename(filename)) {
            mojo.getLog().debug(
                    "Ignoring re Java class file name extension: " +
                            filename
            );
            return Option.none();
        }

        return javaClassFileData(mojo, inputStream, filename);
    }

    static java.util.List<ClassReader> fromJarFile(
            final Mojo mojo,
            final File jarFile
    ) {
        // TODO Only the first .class file encountered in the Jar is logged by this procedure

        mojo.getLog().debug(
                "Examining Jar file " + jarFile.getAbsolutePath()
        );
        try (final JarInputStream jarInputStream = new JarInputStream(
                new FileInputStream(jarFile)
        )
        ) {
            final java.util.List<ClassReader> classReaders = new ArrayList<>();
            while (true) {
                if (jarInputStream.available() <= 0) {
                    break;
                }
                final JarEntry jarEntry = jarInputStream.getNextJarEntry();
                final Option<ClassReader> next = classReaderOf(
                        mojo,
                        jarInputStream,
                        jarEntry.getName()
                );
                if (next.isDefined()) {
                    mojo.getLog().debug("Queueing Java class file: " + jarEntry.getName());
                    classReaders.add(next.get());
                }
            }
            return classReaders;
        } catch (final IOException e) {
            throw new SneakyMojoException("Exception while re-constituting ASM ClassReader instances from Jar file", e);
        }
    }

    static List<ClassReader> fromFile(
            final Mojo mojo,
            final File file
    ) {
        mojo.getLog().debug(
                "Examining file " + file.getAbsolutePath()
        );
        try {
            final java.util.List<ClassReader> classReaders = new ArrayList<>();
            scanFile(mojo, classReaders, file);
            return classReaders;
        } catch (final IOException e) {
            throw new SneakyMojoException("Exception while re-constituting ASM ClassReader instances from files", e);
        }
    }

    private static void scanFile(
            final Mojo mojo,
            final List<ClassReader> classReaders,
            final File entry
    ) throws IOException {
        if (entry.isDirectory()) {
            scanDirectory(mojo, classReaders, entry);
        } else if (entry.isFile()) {
            final Option<ClassReader> classReader = classReaderOf(
                    mojo,
                    new FileInputStream(entry),
                    entry.getName()
            );
            if (classReader.isDefined()) {
                mojo.getLog().debug("Queueing Java class file: " + entry.getAbsolutePath());
                classReaders.add(classReader.get());
            }
        } else {
            mojo.getLog().debug(
                    mojo.getI18nContext().getText(
                            "vivid.cherimoya.message.ignoring-unrecognized-file",
                            entry.getAbsolutePath()
                    )
            );
        }
    }

    private static void scanDirectory(
            final Mojo mojo,
            final List<ClassReader> classReaders,
            final File directory
    ) throws IOException {
        final File[] entries = directory.listFiles();
        if (entries != null) {
            for (final File entry : entries) {
                scanFile(mojo, classReaders, entry);
            }
        }
    }

}

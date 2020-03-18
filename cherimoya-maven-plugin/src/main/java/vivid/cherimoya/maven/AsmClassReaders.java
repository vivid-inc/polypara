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

import io.vavr.collection.Stream;
import io.vavr.control.Option;
import org.apache.commons.compress.utils.IOUtils;
import org.objectweb.asm.ClassReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static vivid.cherimoya.maven.JavaClasses.hasJavaClassFileMagic;

class AsmClassReaders {

    private AsmClassReaders() {
        // Hide the public constructor
    }

    private static Option<ClassReader> classReaderOf(
            final Mojo mojo,
            final Path path
    ) {
        try {
            if (!JavaClasses.isJavaClassFilename(path.toString())) {
                mojo.getLog().debug(
                        "Ignoring re Java class file name extension: " +
                                path
                );
                return Option.none();
            }

            final byte[] bytes = IOUtils.toByteArray(new FileInputStream(path.toFile()));
            if (!hasJavaClassFileMagic(bytes)) {
                mojo.getLog().debug(
                        "Ignoring re Java .class file header magic: " +
                                path
                );
                return Option.none();
            }

            return Option.of(new ClassReader(bytes));
        } catch (final IOException e) {
            throw new SneakyMojoException(
                    CE4ClassReadFailure.asMessage(
                            mojo,
                            path.toString()
                    ),
                    e
            );
        }
    }

    private static Option<ClassReader> classReaderOf(
            final Mojo mojo,
            final File file,
            final JarFile jarFile,
            final JarEntry jarEntry
    ) {
        if (!JavaClasses.isJavaClassFilename(jarEntry.getName())) {
            mojo.getLog().debug(
                    "Ignoring re Java class file name extension: " +
                            Static.pathInJarFile(file, jarEntry.getName())
            );
            return Option.none();
        }

        try (
                final InputStream inputStream = jarFile.getInputStream(jarEntry)
        ) {
            final byte[] bytes = IOUtils.toByteArray(inputStream);
            if (!hasJavaClassFileMagic(bytes)) {
                mojo.getLog().debug(
                        "Ignoring re Java .class file header magic: " +
                                jarEntry.getName()
                );
                return Option.none();
            }

            mojo.getLog().debug("Queueing Java class file: " +
                    Static.pathInJarFile(file, jarEntry.getName())
            );
            return Option.of(
                    new ClassReader(bytes)
            );
        } catch (final IOException e) {
            throw new SneakyMojoException(
                    CE4ClassReadFailure.asMessage(
                            mojo,
                            jarFile.toString()
                    ),
                    e
            );
        }
    }

    private static Stream<JarEntry> streamOfJarEntries(
            final JarFile jarFile
    ) {
        return Stream.ofAll(
                Static.enumerationAsStream(
                        jarFile.entries()
                )
        );
    }

    static java.util.List<ClassReader> fromJarFile(
            final Mojo mojo,
            final File file
    ) {
        mojo.getLog().debug(
                "Examining Jar file " + file.getAbsolutePath()
        );

        try (
                final JarFile jarFile = new JarFile(file)
        ) {
            return streamOfJarEntries(jarFile)
                    .map(e -> classReaderOf(mojo, file, jarFile, e))
                    .flatMap(t -> t)
                    .toJavaList();
        } catch (final IOException e) {
            throw new SneakyMojoException(
                    CE4ClassReadFailure.asMessage(
                            mojo,
                            file.getAbsolutePath()
                    ),
                    e
            );
        }
    }

    private static Stream<Path> streamOfPaths(
            final File file
    ) throws IOException {
        return Stream.ofAll(
                Files.walk(file.toPath())
                        .filter(Files::isRegularFile)
        );
    }

    static List<ClassReader> fromFile(
            final Mojo mojo,
            final File file
    ) {
        mojo.getLog().debug(
                "Examining file " + file.getAbsolutePath()
        );
        try {
            return streamOfPaths(file)
                    .map(p -> classReaderOf(mojo, p))
                    .flatMap(t -> t)
                    .toJavaList();
        } catch (final IOException e) {
            throw new SneakyMojoException(
                    CE4ClassReadFailure.asMessage(
                            mojo,
                            file.getAbsolutePath()
                    ),
                    e
            );
        }
    }

}

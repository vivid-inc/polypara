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
import io.vavr.control.Either;
import io.vavr.control.Option;
import org.apache.commons.compress.utils.IOUtils;
import org.objectweb.asm.ClassReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static java.util.function.Function.identity;
import static vivid.cherimoya.maven.JavaClasses.hasJavaClassFileMagic;

class AsmClassReaders {

    private AsmClassReaders() {
        // Hide the public constructor
    }

    private static Either<Message, Option<ClassReader>> classReaderOf(
            final Mojo mojo,
            final Path path
    ) {
        try {
            if (!JavaClasses.isJavaClassFilename(path.toString())) {
                mojo.getLog().debug(
                        "Ignoring re Java class file name extension: " +
                                path
                );
                return Either.right(Option.none());
            }

            final byte[] bytes = IOUtils.toByteArray(new FileInputStream(path.toFile()));
            if (!hasJavaClassFileMagic(bytes)) {
                mojo.getLog().debug(
                        "Ignoring re Java .class file header magic: " +
                                path
                );
                return Either.right(Option.none());
            }

            return Either.right(
                    Option.of(new ClassReader(bytes))
            );
        } catch (final IOException e) {
            return Either.left(
                    CE4ClassReadFailure.message(
                            path.toString(),
                            e
                    )
            );
        }
    }

    private static Either<Message, Option<ClassReader>> classReaderOf(
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
            return Either.right(Option.none());
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
                return Either.right(Option.none());
            }

            mojo.getLog().debug("Queueing Java class file: " +
                    Static.pathInJarFile(file, jarEntry.getName())
            );
            return Either.right(
                    Option.of(new ClassReader(bytes))
            );
        } catch (final IOException e) {
            return Either.left(
                    CE4ClassReadFailure.message(
                            jarFile.toString(),
                            e
                    )
            );
        }
    }

    private static Either<Message, Stream<JarEntry>> streamOfJarEntries(
            final JarFile jarFile
    ) {
        return Either.right(
                Stream.ofAll(
                        Static.enumerationAsStream(
                                jarFile.entries()
                        )
                )
        );
    }

    static Either<Message, Stream<ClassReader>> fromJarFile(
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
                    .map(val -> val
                            .flatMap(entry -> classReaderOf(mojo, file, jarFile, entry))
                            .flatMap(identity()));
        } catch (final IOException e) {
            return Either.left(
                    CE4ClassReadFailure.message(
                            file.getAbsolutePath(),
                            e
                    )
            );
        }
    }

    private static Either<Message, Stream<Path>> streamOfPaths(
            final File file
    ) {
        try {
            return Either.right( Stream.ofAll(
                    Files.walk(file.toPath())
                            .filter(Files::isRegularFile)));
        } catch (final IOException e) {
            return Either.left(
                    CE4ClassReadFailure.message(
                            file.getAbsolutePath(),
                            e
                    )
            );
        }
    }

    static Either<Message, Stream<ClassReader>> fromFile(
            final Mojo mojo,
            final File file
    ) {
        mojo.getLog().debug(
                "Examining file " + file.getAbsolutePath()
        );

        return streamOfPaths(file)
                .map(val -> val
                        .flatMap(path -> classReaderOf(mojo, path))
                        .flatMap(identity()));
    }

}

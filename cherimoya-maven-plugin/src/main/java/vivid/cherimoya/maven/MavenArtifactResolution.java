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

import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Stream;
import io.vavr.control.Either;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.objectweb.asm.ClassReader;

import java.io.File;

/**
 * Resolve Maven artifacts by version.
 */
class MavenArtifactResolution {

    private MavenArtifactResolution() {
        // Hide the public constructor
    }

    static Either<Message, Map<String, Stream<ClassReader>>> mapVersionsToClassReaders(
            final Mojo mojo,
            final List<String> resolvableVersions
    ) {
        final Either<Message, Stream<ClassReader>> currentVersionClassReaders =
                AsmClassReaders.fromFile(
                        mojo,
                        new File( mojo.getMavenProject().getBuild().getOutputDirectory() )
                );
        final Either<Message, Map<String, Stream<ClassReader>>> mapping =
                currentVersionClassReaders.map(val ->
                        HashMap.of(
                                mojo.getMavenProject().getVersion(),
                                val
                        )
                );

        return
                resolvableVersions.foldLeft(
                        mapping,
                        (m, version) -> {
                            if (mapping.isLeft()) {
                                return mapping;
                            }
                            final Either<Message, Stream<ClassReader>> ret = classReaderForResolvableVersion(
                                    mojo,
                                    mojo.getMavenProject().getGroupId(),
                                    mojo.getMavenProject().getArtifactId(),
                                    version
                            );
                            if (ret.isLeft()) {
                                return Either.left(ret.getLeft());
                            }

                            return Either.right(
                                    mapping.get().put(
                                            version,
                                            ret.get()
                                    )
                            );
                        }
                );
    }

    private static Either<Message, Stream<ClassReader>> classReaderForResolvableVersion(
            final Mojo mojo,
            final String groupId,
            final String artifactId,
            final String version
    ) {
        return
                resolveArtifact(mojo, groupId, artifactId, version)
                        .flatMap(ar -> AsmClassReaders.fromJarFile(mojo, ar.getArtifact().getFile()));
    }

    private static Either<Message, ArtifactResult> resolveArtifact(
            final Mojo mojo,
            final String groupId,
            final String artifactId,
            final String version
    ) {
        final String gav = Static.mavenGAVOf(
                groupId,
                artifactId,
                version
        );

        final Artifact artifact = new DefaultArtifact(gav);

        ArtifactRequest artifactRequest = new ArtifactRequest();
        artifactRequest.setArtifact(artifact);
        artifactRequest.setRepositories(mojo.getRemoteRepositories());

        try {
            return Either.right(
                    mojo.getRepositorySystem().resolveArtifact(
                            mojo.getRepositorySystemSession(),
                            artifactRequest
                    )
            );
        } catch (final ArtifactResolutionException e) {
            return Either.left(
                    CE3ArtifactResolutionFailure.message(
                            gav,
                            e
                    )
            );
        }
    }

}

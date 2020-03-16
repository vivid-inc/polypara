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
import io.vavr.collection.Map;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.objectweb.asm.ClassReader;

import java.io.File;

class ArtifactResolution {

    private ArtifactResolution() {
        // Hide the public constructor
    }

    static Map<String, java.util.List<ClassReader>> mapVersionsToClassLoaders(
            final Mojo mojo,
            final List<String> resolvableVersions
    ) {
        return resolvableVersions.toMap(
                v -> v,
                v -> classLoaderForResolvableVersion(
                        mojo,
                        mojo.getMavenProject().getGroupId(),
                        mojo.getMavenProject().getArtifactId(),
                        v)
        )
                .put(
                        mojo.getMavenProject().getVersion(),
                        AsmClassReaders.fromFile(
                                mojo,
                                new File(
                                        mojo.getMavenProject().getBuild().getOutputDirectory()
                                )
                        )
                );
    }

    private static java.util.List<ClassReader> classLoaderForResolvableVersion(
            final Mojo mojo,
            final String groupId,
            final String artifactId,
            final String version
    ) {
        final ArtifactResult artifactResult = resolveArtifact(
                mojo,
                groupId, artifactId, version
        );

        return AsmClassReaders.fromJarFile(
                mojo,
                artifactResult.getArtifact().getFile()
        );
    }

    private static ArtifactResult resolveArtifact(
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
            return mojo.getRepositorySystem().resolveArtifact(
                    mojo.getRepositorySystemSession(),
                    artifactRequest
            );
        } catch (final ArtifactResolutionException e) {
            throw new SneakyMojoException(
                    mojo.getI18nContext().getText(
                            "vivid.cherimoya.error.ce-3-artifact-resolution-failure",
                            gav
                    ),
                    e
            );
        }
    }

}

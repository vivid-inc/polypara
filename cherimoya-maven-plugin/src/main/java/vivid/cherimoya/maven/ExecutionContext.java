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

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;

import java.util.List;

class ExecutionContext {

    final I18nContext i18NContext;
    final Log log;
    final MavenProject mavenProject;
    final List<RemoteRepository> remoteRepositories;
    final RepositorySystem repositorySystem;
    final RepositorySystemSession repositorySystemSession;

    ExecutionContext(
            final I18nContext i18NContext,
            final Log log,
            final MavenProject mavenProject,
            final List<RemoteRepository> remoteRepositories,
            final RepositorySystem repositorySystem,
            final RepositorySystemSession repositorySystemSession
    ) {
        this.i18NContext = i18NContext;
        this.log = log;
        this.mavenProject = mavenProject;
        this.remoteRepositories = remoteRepositories;
        this.repositorySystem = repositorySystem;
        this.repositorySystemSession = repositorySystemSession;
    }

}

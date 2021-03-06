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

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;

import java.util.List;

/**
 * Provide sub-system access to the Mojo's execution context.
 */
interface Mojo {

    I18nContext getI18nContext();
    Log getLog();
    MavenProject getMavenProject();
    VerifyConstantsMojo.ReportingLevel getReportingLevel();
    List<RemoteRepository> getRemoteRepositories();
    RepositorySystem getRepositorySystem();
    RepositorySystemSession getRepositorySystemSession();

}

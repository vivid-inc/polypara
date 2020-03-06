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
import org.codehaus.plexus.i18n.I18N;

/**
 * @since 1.0
 */
class ExecutionContext {

//    final Data data;
    final I18nLogging log;
    final MavenProject project;

    ExecutionContext(
            final I18N i18n,
            final Log rawLogger,
            final MavenProject project
    ) {
//        this.data = new Data();
        this.log = new I18nLogging(i18n, rawLogger);
        this.project = project;
    }

}

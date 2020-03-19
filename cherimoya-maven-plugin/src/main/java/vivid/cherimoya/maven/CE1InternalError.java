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

import org.apache.maven.plugin.MojoExecutionException;

/**
 * @since 1.0
 */
class CE1InternalError {

    private CE1InternalError() {
        // Hide the public constructor
    }

    static MojoExecutionException asNewMojoExecutionException(
            final Mojo mojo,
            final String message
    ) {
        return new MojoExecutionException(
                mojo.getI18nContext().getText(
                        "vivid.cherimoya.error.ce-1-internal-error",
                        message
                )
        );
    }

    static MojoExecutionException asNewMojoExecutionException(
            final Mojo mojo,
            final String message,
            final Exception ex
    ) {
        return new MojoExecutionException(
                mojo.getI18nContext().getText(
                        "vivid.cherimoya.error.ce-1-internal-error",
                        message
                ),
                ex
        );
    }

}
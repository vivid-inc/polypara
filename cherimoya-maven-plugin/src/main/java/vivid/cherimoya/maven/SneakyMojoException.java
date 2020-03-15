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

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Intentionally wrap checked exceptions in unchecked exceptions for the
 * express purposes of:
 *
 * 1. Reducing exception-handling code within lambdas.
 * 2. Passing informative error messages in-situ to users.
 *
 * Unwrap the causative exception and handle it on the outside with familiar
 * try-catch semantics via {@code ex.getCause()}.
 */
@SuppressWarnings("serial")
class SneakyMojoException extends RuntimeException {

    SneakyMojoException(final String message, final Throwable cause) { super(message, cause); }

    static void unwrap(
            final SneakyMojoException ex
    ) throws MojoExecutionException {
        // Unwrap the causative Throwable and report it to Maven
        final Throwable cause = ex.getCause();
        throw new MojoExecutionException(
                cause.getMessage(),
                cause
        );
    }

}

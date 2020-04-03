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

import io.vavr.control.Option;

/**
 * @since 0.2.0
 */
class CE4ClassReadFailure implements Message {

    private static final String I18N_KEY = "vivid.cherimoya.error.ce-4-class-read-failure";

    private final Option<Exception> cause;
    private final String path;

    private CE4ClassReadFailure(
            final String path,
            final Option<Exception> cause
    ) {
        this.path = path;
        this.cause = cause;
    }

    static Message message(
            final String path
    ) {
        return new CE4ClassReadFailure(path, Option.none());
    }

    static Message message(
            final String path,
            final Exception cause
    ) {
        return new CE4ClassReadFailure(path, Option.of(cause));
    }

    @Override
    public Option<Exception> getCause() {
        return cause;
    }

    public String render(
            final Mojo mojo
    ) {
        return mojo.getI18nContext().getText(
                I18N_KEY,
                path
        );
    }

}

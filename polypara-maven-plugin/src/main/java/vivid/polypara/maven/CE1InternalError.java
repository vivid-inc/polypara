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

import io.vavr.control.Option;

/**
 * @since 0.2.0
 */
class CE1InternalError implements Message {

    private static final String I18N_KEY = "vivid.polypara.error.ce-1-internal-error";

    private final Option<Exception> cause;
    private final String message;

    private CE1InternalError(
            final Option<Exception> cause,
            final String message
    ) {
        this.cause = cause;
        this.message = message;
    }

    static Message message(
            final String message
    ) {
        return new CE1InternalError(
                Option.none(),
                message
        );
    }

    static Message message(
            final String message,
            final Exception cause
    ) {
        return new CE1InternalError(
                Option.of(cause),
                message
        );
    }

    @Override
    public Option<Exception> getCause() {
        return cause;
    }

    @Override
    public String render(
            final Mojo mojo
    ) {
        return mojo.getI18nContext().getText(
                I18N_KEY,
                message
        );
    }

}

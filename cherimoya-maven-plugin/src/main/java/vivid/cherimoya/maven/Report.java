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

import io.vavr.collection.List;
import io.vavr.control.Option;

import java.util.function.Consumer;

class Report {

    private static final int ABBREVIATED_VALUE_LENGTH_STRING_FORMAT = 80;

    private Report() {
        // Hide the public constructor
    }

    private static String objectValueAsAbbreviatedString(
            final Object obj
    ) {
        final String str = String.valueOf(obj);
        return str.length() < ABBREVIATED_VALUE_LENGTH_STRING_FORMAT ?
                str :
                String.format(
                        "%s ...",
                        str.substring(0, ABBREVIATED_VALUE_LENGTH_STRING_FORMAT)
                );
    }

    static void report(
            final Mojo mojo,
            final List<ConstancyViolation> constancyViolations
    ) {
        mojo.getLog().info("");
        final Consumer<String> log = logLine(mojo);
        constancyViolations.forEach(
                v -> report(mojo, log, v)
        );
    }

    private static void report(
            final Mojo mojo,
            final Consumer<String> log,
            final ConstancyViolation constancyViolation
    ) {
        final Option<Integer> versionRangeColumnWidth = constancyViolation.fieldValueByVersionRange.map(x -> x._1)
                .map(SimpleVersionRange::toString)
                .map(String::length)
                .max();
        if (versionRangeColumnWidth.isEmpty()) {
            throw new SneakyMojoException(
                    mojo.getI18nContext().getText(
                            "vivid.cherimoya.error.ce-1-internal-error",
                            "Could not calculate the width of the version range column"
                    ),
                    new IllegalStateException()
            );
        }

        log.accept(mojo.getI18nContext().getText(
                "vivid.cherimoya.report.field-value-constancy-violation",
                constancyViolation.fieldName
        ));
        constancyViolation.fieldValueByVersionRange.forEach(
                v -> log.accept(
                        "  " + fieldValue(mojo, v._1, versionRangeColumnWidth.get(), v._2)
                )
        );
        log.accept("");
    }

    private static String fieldValue(
            final Mojo mojo,
            final SimpleVersionRange versionRange,
            final int versionRangeColumnWidth,
            final Option<Object> fieldValue
    ) {
        final String versionDescription =
                String.format(
                        String.format("%%-%ds", versionRangeColumnWidth),
                        versionRange
                );
        if (fieldValue.isDefined()) {
            return mojo.getI18nContext().getText(
                    "vivid.cherimoya.report.field-value-constancy-violation-field-value",
                    versionDescription,
                    objectValueAsAbbreviatedString(fieldValue.get())
            );
        } else {
            return mojo.getI18nContext().getText(
                    "vivid.cherimoya.report.field-value-constancy-violation-field-absent",
                    versionDescription
            );
        }
    }

    private static Consumer<String> logLine(
            final Mojo mojo
    ) {
        if (mojo.getReportingLevel() == VerifyConstantsMojo.ReportingLevel.ERROR) {
            return mojo.getLog()::error;
        } else if (mojo.getReportingLevel() == VerifyConstantsMojo.ReportingLevel.WARNING) {
            return mojo.getLog()::warn;
        } else {
            throw new SneakyMojoException(
                    // TODO Create a convenience method in SneakyMojoException
                    mojo.getI18nContext().getText(
                            "vivid.cherimoya.error.ce-1-internal-error",
                            String.format(
                                    "Unexpected ReportingLevel value: %s",
                                    mojo.getReportingLevel()
                            )
                    ),
                    new IllegalStateException()
            );
        }
    }

}

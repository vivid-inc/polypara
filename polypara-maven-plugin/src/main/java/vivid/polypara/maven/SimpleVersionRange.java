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
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import java.util.Objects;

/**
 * Simplistic encoding of a version range with a {@code start} version and an optional {@code end} version.
 */
class SimpleVersionRange implements Comparable<SimpleVersionRange> {

    final String start;
    final Option<String> end;

    SimpleVersionRange(
            final String start,
            final Option<String> end
    ) {
        Objects.requireNonNull(start, "start is null");
        Objects.requireNonNull(end, "end is null");
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        return String.format(
                "%s%s",
                this.start,
                this.end.isDefined() ? String.format(" ~ %s", this.end.get()) : ""
        );
    }

    @Override
    public int compareTo(final SimpleVersionRange o) {
        return new DefaultArtifactVersion(this.start)
                .compareTo(new DefaultArtifactVersion(o.start));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final SimpleVersionRange that = (SimpleVersionRange) o;
        return start.equals(that.start) &&
                end.equals(that.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

}

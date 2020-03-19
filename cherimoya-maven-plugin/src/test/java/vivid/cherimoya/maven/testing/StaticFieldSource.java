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

package vivid.cherimoya.maven.testing;

import io.vavr.Value;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.support.AnnotationConsumer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.stream.Stream;

/*
 * Referencing https://www.baeldung.com/parameterized-tests-junit-5
 */
class StaticFieldArgumentsProvider
        implements ArgumentsProvider, AnnotationConsumer<StaticFieldSource> {

    private String fieldName;

    @Override
    public Stream<? extends Arguments> provideArguments(
            final ExtensionContext extensionContext
    ) {
        return extensionContext.getTestClass()
                .map(this::getField)
                .map(this::getValue)
                .map(Value::toJavaStream)
                .orElseThrow(() ->
                        new IllegalArgumentException("Could not obtain Arguments from value of field: " + fieldName));
    }

    @Override
    public void accept(
            final StaticFieldSource variableSource
    ) {
        fieldName = variableSource.value();
    }

    private Field getField(
            final Class<?> clazz
    ) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (final Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private Value<Arguments> getValue(
            final Field field
    ) {
        Object value = null;
        try {
            value = field.get(null);
        } catch (final Exception ignored) {}

        return value == null ? null : (Value<Arguments>) value;
    }

}

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ArgumentsSource(StaticFieldArgumentsProvider.class)
public @interface StaticFieldSource {

    /*
     * Name of the field from whose value arguments are sourced.
     */
    String value();

}

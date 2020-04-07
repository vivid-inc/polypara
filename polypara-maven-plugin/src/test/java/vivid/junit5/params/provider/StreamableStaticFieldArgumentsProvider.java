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

package vivid.junit5.params.provider;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.AnnotationConsumer;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.stream.Stream;

class StreamableStaticFieldArgumentsProvider
        implements ArgumentsProvider, AnnotationConsumer<StreamableStaticFieldSource> {

    private String fieldName;

    @Override
    public void accept(
            final StreamableStaticFieldSource variableSource
    ) {
        fieldName = variableSource.value();
    }


    @Override
    public Stream<? extends Arguments> provideArguments(
            final ExtensionContext extensionContext
    ) {
        return extensionContext.getTestClass()
                .map(this::getField)
                .map(this::getFieldValue)
                .map(this::toStream)
                .orElseThrow(() ->
                        new IllegalArgumentException("Could not obtain stream of Arguments from value of field: " + fieldName));
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

    private Object getFieldValue(
            final Field field
    ) {
        try {
            return field.get(null);
        } catch (final Exception ignored) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private Stream<Arguments> toStream(
            final Object val
    ) {
        try {
            final Method javaM = val.getClass().getMethod("stream");
            return (Stream<Arguments>) javaM.invoke(val);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
        }

        try {
            final Method vavrM = val.getClass().getMethod("toJavaStream");
            return (Stream<Arguments>) vavrM.invoke(val);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
        }

        return null;
    }

}

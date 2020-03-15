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

import io.vavr.Tuple2;
import io.vavr.collection.List;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.ArrayList;

// TODO Print .class file paths within jars like this sample from maven-compiler-plugin:    /home/ty/.m2/repository/org/neo4j/neo4j-graphdb-api/4.0.0/neo4j-graphdb-api-4.0.0.jar(org/neo4j/dbms/api/DatabaseManagementService.class)

class AsmFieldAnnotationScanner extends AsmClassVisitorAdapter<List<Tuple2<String, Object>>> {

    private final ArrayList<Tuple2<String, Object>> accumulator;
    private Mojo mojo;
    private final Class<?> annotationClass;

    private String clazzName;

    AsmFieldAnnotationScanner(
            final Mojo mojo,
            final Class<?> annotationClass
    ) {
        super(Opcodes.ASM7);
        this.mojo = mojo;
        this.annotationClass = annotationClass;

        this.accumulator = new ArrayList<>();
    }

    @Override
    public void visit(
            final int version,
            final int access,
            final String name,
            final String signature,
            final String superName,
            final String[] interfaces
    ) {
        this.clazzName = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public FieldVisitor visitField(
            final int access,
            final String name,
            final String typeDescriptor,
            final String signature,
            final Object value
    ) {
        return new FieldAnnotationScanner(name, value);
    }

    @Override
    void logStart() {
        mojo.getLog().debug(
                "Scanning for class fields annotated with: " + annotationClass
        );
    }

    @Override
    List<Tuple2<String, Object>> accumulateResult() {
        return List.ofAll(accumulator);
    }

    private class FieldAnnotationScanner extends FieldVisitor {

        private final String annotationDescriptor = Type.getType(annotationClass).getDescriptor();

        private boolean isTargetAnnotation(
                final String fieldTypeDescriptor
        ) {
            return annotationDescriptor.equals(fieldTypeDescriptor);
        }

        private final String fieldName;
        private Object fieldValue;

        private FieldAnnotationScanner(
                final String fieldName,
                final Object fieldValue
        ) {
            super(Opcodes.ASM5);
            this.fieldName = fieldName;
            this.fieldValue = fieldValue;
        }

        @Override
        public AnnotationVisitor visitAnnotation(
                final String descriptor,
                final boolean visible
        ) {
            if (isTargetAnnotation(descriptor)) {
                accumulator.add(new Tuple2<>(
                        Static.fieldFullyQualifiedName(
                                Type.getObjectType(clazzName).getClassName(),
                                fieldName
                        ),
                        fieldValue));
            }
            return super.visitAnnotation(descriptor, visible);
        }
    }

}

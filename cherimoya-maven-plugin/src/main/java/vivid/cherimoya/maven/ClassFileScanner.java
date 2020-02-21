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

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.objectweb.asm.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

// TODO Try https://github.com/ronmamo/reflections as demonstrated in swagger-maven-plugin/src/main/java/io/openapitools/swagger/JaxRSScanner.java

/**
 * @since 1.0
 */
class ClassFileScanner {

    private final Factory factory;
    private final String artifactVersion; // TODO Instead of version, pass the Maven artifact object representing a JAR or target/classes dir, + artifact version.

    ClassFileScanner(
            final Factory factory,
            final String artifactVersion
    ) {
        this.factory = factory;
        this.artifactVersion = artifactVersion;
    }

    void scanAll() throws DependencyResolutionRequiredException {
        for (final String entry : (List<String>) factory.project.getCompileClasspathElements()) {
            scanEntry(new File(entry));
        }
    }

    private void scanEntry(final File entry) {
        if (entry.isDirectory()) {
            scanDirectory(entry);
        } else if (Static.isJavaClassFile(entry)) {
            processJavaClassFile(entry);
        } else {
            factory.log.debug("vivid.cherimoya.message.ignoring-unrecognized-file", entry);
        }
    }

    private void scanDirectory(final File directory) {
        final File[] entries = directory.listFiles();
        if (entries != null) {
            for (final File entry : entries) {
                scanEntry(entry);
            }
        }
    }

    private void processJavaClassFile(final File javaClassFile) {
        try {
            final ClassReader classReader = new ClassReader(new FileInputStream(javaClassFile));
            classReader.accept(new FieldScanner(), 0);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private class FieldScanner extends ClassVisitor {
        private String clazzName;

        FieldScanner() {
            super(Opcodes.ASM5);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            this.clazzName = name;
            super.visit(version, access, name, signature, superName, interfaces);
        }

        @Override
        public FieldVisitor visitField(int access, String name, String typeDescriptor, String signature, Object value) {
            //[INFO] access 24, name VIVID_SUPPORT_ENTITLEMENT_NUMBER_KEY, desc Ljava/lang/String;, signature null, value v
            return new FieldAnnotationScanner(name);
        }

        class FieldAnnotationScanner extends FieldVisitor {
            private final String fieldName;

            FieldAnnotationScanner(
                    final String fieldName
            ) {
                super(Opcodes.ASM5);
                this.fieldName = fieldName;
            }

            //[INFO] visitAnnotation() desc = Lvivid/cherimoya/annotation/Constant;, visible = false
            @Override
            public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                if (Static.isConstantAnnotation(desc)) {
                    factory.data.recordConstantField(artifactVersion, clazzName, fieldName);
                }
                return super.visitAnnotation(desc, visible);
            }
        }
    }

}

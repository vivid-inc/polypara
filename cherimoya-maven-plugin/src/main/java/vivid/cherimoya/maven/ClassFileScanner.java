package vivid.cherimoya.maven;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.objectweb.asm.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * @since 1.0
 */
public class ClassFileScanner {

    private final Factory factory;

    public ClassFileScanner(
            final Factory factory
    ) {
        this.factory = factory;
    }

    public void scanAll() throws DependencyResolutionRequiredException {
        for (final String entry : (List<String>) factory.project.getCompileClasspathElements()) {
            scanEntry(new File(entry));
        }
    }

    private void scanEntry(final File entry) {
        if (entry.isDirectory()) {
            scanDirectory(entry);
        } else if (IsJavaClassFilePredicate.SINGLETON.apply(entry)) {
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

    class FieldScanner extends ClassVisitor {

        public FieldScanner() {
            super(Opcodes.ASM5);
        }

        @Override
        public FieldVisitor visitField(int access, String name, String typeDescriptor, String signature, Object value) {
            //[INFO] access 24, name VIVID_SUPPORT_ENTITLEMENT_NUMBER_KEY, desc Ljava/lang/String;, signature null, value v
            return new FieldAnnotationScanner(name);
        }

        class FieldAnnotationScanner extends FieldVisitor {
            private final String fieldName;

            public FieldAnnotationScanner(
                    final String fieldName
            ) {
                super(Opcodes.ASM5);
                this.fieldName = fieldName;
            }

            //[INFO] visitAnnotation() desc = Lvivid/cherimoya/annotation/Constant;, visible = false
            @Override
            public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                if (IsConstantAnnotationPredicate.SINGLETON.apply(desc)) {
                    // TODO Record the fully-qualified classname, field name, field type, and field value
                    factory.log.info("@Constant " + fieldName);
                }
                return super.visitAnnotation(desc, visible);
            }
        }
    }

}

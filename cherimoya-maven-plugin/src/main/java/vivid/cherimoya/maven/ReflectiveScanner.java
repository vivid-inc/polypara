package vivid.cherimoya.maven;

import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.util.Set;

public class ReflectiveScanner {

    public static Set<Field> scan(
            final Class<? extends Annotation> annotation,
            final URLClassLoader cl
    ) {
        final Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .setUrls(ClasspathHelper.forClassLoader(cl))
                        .addClassLoader(cl)
                        .setScanners(
                                new FieldAnnotationsScanner(),
                                new SubTypesScanner(false)
                        )
                        .useParallelExecutor()
        );

        return reflections.getFieldsAnnotatedWith(annotation);
    }

}

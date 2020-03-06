package vivid.cherimoya.maven;

import io.vavr.collection.Set;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import vivid.cherimoya.annotation.Constant;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.Collection;
import java.util.Optional;

public class ReflectiveScanner {

    public static void scan(
//            final Collection<URL> resourcePaths
    ) {
        final Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .addScanners(
                                new FieldAnnotationsScanner()
                        )
                        .addUrls(ClasspathHelper.forClassLoader())
                        .useParallelExecutor()
        );


        System.out.println("** FIELDS");
        java.util.Set<Field> x = reflections.getFieldsAnnotatedWith(Constant.class);
        for (final Field f : x) {
            System.out.println(f);
        }
    }

}

package vivid.cherimoya.maven;

import com.google.common.base.Predicate;

import java.io.File;

/**
 * @since 1.0
 */
public class IsJavaClassFilePredicate implements Predicate<File> {

    private static final String JAVA_CLASS_FILE_EXTENSION = "class";

    public static final IsJavaClassFilePredicate SINGLETON = new IsJavaClassFilePredicate();

    @Override
    public boolean apply(final File file) {
        return file.isFile() && file.getName().endsWith("." + JAVA_CLASS_FILE_EXTENSION);
    }

}

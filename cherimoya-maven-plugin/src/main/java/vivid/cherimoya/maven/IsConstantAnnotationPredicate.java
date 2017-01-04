package vivid.cherimoya.maven;

import com.google.common.base.Predicate;
import org.objectweb.asm.Type;
import vivid.cherimoya.maven.annotation.Constant;

/**
 * @since 1.0
 */
public class IsConstantAnnotationPredicate implements Predicate<String> {

    private static final String CONSTANT_DESCRIPTOR = Type.getType(Constant.class).getDescriptor();

    public static final IsConstantAnnotationPredicate SINGLETON = new IsConstantAnnotationPredicate();

    @Override
    public boolean apply(final String descriptor) {
        return CONSTANT_DESCRIPTOR.equals(descriptor);
    }

}

package vivid.cherimoya.maven.test;

import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;
import org.junit.Test;
import vivid.cherimoya.maven.Static;
import vivid.cherimoya.maven.VerifyConstantsMojo;

import java.io.File;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SimpleTest {

    @Rule
    public MojoRule rule = new MojoRule() {};

    @Test
    public void testSimple() throws Exception {
        final File pom = new File("target/test-classes/simple-project");
        assertNotNull(pom);
        assertTrue(pom.exists());

        final VerifyConstantsMojo mojo =
                (VerifyConstantsMojo) rule.lookupConfiguredMojo(
                        pom,
                        Static.POM_CHERIMOYA_VERIFY_MOJO_NAME
                );
        assertNotNull(mojo);
        mojo.execute();
    }

}
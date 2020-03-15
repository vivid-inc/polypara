package vivid.cherimoya.maven;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.MavenArtifactRepository;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import vivid.cherimoya.maven.Static;
import vivid.cherimoya.maven.VerifyConstantsMojo;

import java.io.File;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@Ignore
public class SimpleTest {

    // https://stackoverflow.com/questions/45241317/hard-time-mojo-testing-with-maven-plugin-testing-harness

    private static final String SIMPLE_PROJECT_DIR = "target/test-classes/simple-project";

    @Rule
    public MojoRule mojoRule = new MojoRule();

    @Test
    public void testSimple() throws Exception {
        final File pomDir = new File(SIMPLE_PROJECT_DIR);
        assertNotNull(pomDir);
        assertTrue(pomDir.exists());

        runMavenInSubProcess(pomDir, "mvn", "compile");

        final VerifyConstantsMojo verifyConstantsMojo =
                (VerifyConstantsMojo) mojoRule.lookupConfiguredMojo(
                        pomDir,
                        Static.POM_CHERIMOYA_VERIFY_MOJO_NAME
                );
        assertNotNull(verifyConstantsMojo);
        verifyConstantsMojo.execute();
    }

    private static void runMavenInSubProcess(
            final File directory,
            final String... command
    ) throws Exception {
        final Process process = new ProcessBuilder()
                .directory(directory)
                .command(command)
                .inheritIO()
                .start();
        int exitCode = process.waitFor();
        assertThat(exitCode, is(0));
    }

    private static ArtifactRepository createLocalArtifactRepository() {
        return new MavenArtifactRepository("local",
                new File("/tmp/repo").toURI().toString(),
                new DefaultRepositoryLayout(),
                new ArtifactRepositoryPolicy( true, ArtifactRepositoryPolicy.UPDATE_POLICY_ALWAYS, ArtifactRepositoryPolicy.CHECKSUM_POLICY_IGNORE ),
                new ArtifactRepositoryPolicy( true, ArtifactRepositoryPolicy.UPDATE_POLICY_ALWAYS, ArtifactRepositoryPolicy.CHECKSUM_POLICY_IGNORE )

        );
    }

}

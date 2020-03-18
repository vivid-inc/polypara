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

//@Ignore
public class SimpleTest {
/*
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
*/
}

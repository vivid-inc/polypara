package vivid.cherimoya.maven;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.i18n.I18N;

/**
 * @since 1.0
 */
public class Factory {

    public final I18nLogging log;
    public final MavenProject project;

    public Factory(
            final I18N i18n,
            final Log rawLogger,
            final MavenProject project
    ) {
        this.project = project;

        this.log = new I18nLogging(i18n, rawLogger);
    }

}

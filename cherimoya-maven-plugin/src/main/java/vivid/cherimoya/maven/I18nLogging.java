package vivid.cherimoya.maven;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.i18n.I18N;

import java.util.Locale;

/**
 * @since 1.0
 */
public class I18nLogging {

    private static final String I18N_RESOURCE_BUNDLE = "cherimoya-i18n";

    private final I18N i18n;
    private final Log log;

    public I18nLogging(
            final I18N i18n,
            final Log log
    ) {
        this.i18n = i18n;
        this.log = log;
    }

    private String getText(
            final String i18nKey,
            final Object... args
    ) {
        final Locale locale = Locale.getDefault();
        return i18n.format(I18N_RESOURCE_BUNDLE, locale, i18nKey, args);
    }

    public void debug(
            final String i18nKey,
            final Object... args
    ) {
        log.debug(getText(i18nKey, args));
    }

    public void info(
            final String i18nKey,
            final Object... args
    ) {
        log.info(getText(i18nKey, args));
    }

    public void warn(
            final String i18nKey,
            final Object... args
    ) {
        log.warn(getText(i18nKey, args));
    }

}

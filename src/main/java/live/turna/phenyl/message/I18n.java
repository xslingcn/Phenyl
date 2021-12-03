package live.turna.phenyl.message;

import live.turna.phenyl.PhenylBase;

import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * I18n for Phenyl messages.
 * Credit: com.earth2me.essentials.I18n
 *
 * @author xsling
 * @version 1.0
 * @since 2021/12/3 17:45
 */
public class I18n extends PhenylBase {
    private static final String MESSAGES = "messages";
    private static final Pattern NODOUBLEMARK = Pattern.compile("''");
    private static final ResourceBundle NULL_BUNDLE = new ResourceBundle() {
        @Override
        protected Object handleGetObject(String key) {
            return null;
        }

        @Override
        public Enumeration<String> getKeys() {
            return null;
        }
    };
    private static I18n instance;
    private final transient Locale defaultLocale = Locale.getDefault();
    private final transient ResourceBundle defaultBundle;
    private transient Locale currentLocale = defaultLocale;
    private transient ResourceBundle customBundle;
    private transient ResourceBundle localeBundle;
    private transient Map<String, MessageFormat> messageFormatCache = new HashMap<>();


    public void onEnable() {
        instance = this;
    }

    public void onDisable() {
        instance = null;
    }

    public I18n() {
        defaultBundle = ResourceBundle.getBundle(MESSAGES, Locale.ENGLISH);
        localeBundle = defaultBundle;
        customBundle = NULL_BUNDLE;
    }

    /**
     * Produce message corresponding to locale.
     *
     * @param key     Keys of messages to locate in properties.
     * @param objects Append objects.
     * @return String Formatted string
     */
    public static String i18n(final String key, final Object... objects) {
        if (instance == null) {
            return "";
        }
        if (objects.length == 0) {
            return NODOUBLEMARK.matcher(instance.translate(key)).replaceAll("'");
        }
        return instance.format(key, objects);
    }

    /**
     * Format messages.
     *
     * @param key     Keys of messages to locate in properties.
     * @param objects Append objects.
     * @return MessageFormat Formatted string.
     */
    public String format(final String key, final Object... objects) {
        String format = translate(key);
        MessageFormat messageFormat = messageFormatCache.get(format);
        if (messageFormat == null) {
            try {
                messageFormat = new MessageFormat(format);
            } catch (final IllegalArgumentException e) {
                LOGGER.severe(i18n("invalidTransKey", key, e.getLocalizedMessage()));
                format = format.replaceAll("\\{(\\D*?)", "\\[$1\\]");
                messageFormat = new MessageFormat(format);
            }
            messageFormatCache.put(format, messageFormat);
        }
        return messageFormat.format(objects).replace(' ', ' ');
    }

    /**
     * Find the corresponding message string from properties.
     *
     * @param key Keys of messages to locate in properties.
     * @return String Found string.
     */
    private String translate(final String key) {
        try {
            try {
                return customBundle.getString(key);
            } catch (final MissingResourceException e) {
                return localeBundle.getString(key);
            }
        } catch (final MissingResourceException e) {
            LOGGER.warning(i18n("missingTransKey", e.getKey(), localeBundle.getLocale().toString()));
            return defaultBundle.getString(key);
        }
    }

    /**
     * Update the locale.
     *
     * @param locale The locale to be updated to.
     */
    public void updateLocale(final String locale) {
        if (locale != null && !locale.isEmpty()) {
            final String[] parts = locale.split("[_]");
            if (parts.length == 1) {
                currentLocale = new Locale(parts[0]);
            }
            if (parts.length == 2) {
                currentLocale = new Locale(parts[0], parts[1]);
            }
            if (parts.length == 3) {
                currentLocale = new Locale(parts[0], parts[1], parts[2]);
            }
        }
        ResourceBundle.clearCache();
        messageFormatCache = new HashMap<>();

        try {
            localeBundle = ResourceBundle.getBundle(MESSAGES, currentLocale);
            LOGGER.info(i18n("usingLocale", currentLocale.toString()));
        } catch (final MissingResourceException e) {
            localeBundle = NULL_BUNDLE;
            LOGGER.warning(String.format("Locale %s not found! Falling back to English.", currentLocale));
            this.updateLocale("en");
        }

    }


}
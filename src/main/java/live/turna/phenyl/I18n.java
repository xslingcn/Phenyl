package live.turna.phenyl;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * @author xsling
 * @version 1.0
 * @since 2021/12/3 17:45
 * Credit: com.earth2me.essentials.I18n
 */
public class I18n {
    static final Logger LOGGER = Logger.getLogger("Phenyl");
    private static final String MESSAGES = "messages";
    private static final Pattern NODOUBLEMARK = Pattern.compile("''");
    private static final ResourceBundle NULL_BUNDLE = new ResourceBundle() {
        @Override
        protected Object handleGetObject(@NotNull String key) {
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
    private final transient Phenyl phenyl;
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
        this.phenyl = Phenyl.getInstance();
        defaultBundle = ResourceBundle.getBundle(MESSAGES, Locale.ENGLISH);
        localeBundle = defaultBundle;
        customBundle = NULL_BUNDLE;
    }

    /**
     * 
     * @param key Keys of messages to locate in properties.
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
     * 
     * @param key Keys of messages to locate in properties.
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
                LOGGER.severe("Invalid Translation key for '" + key + "': " + e.getMessage());
                format = format.replaceAll("\\{(\\D*?)", "\\[$1\\]");
                messageFormat = new MessageFormat(format);
            }
            messageFormatCache.put(format, messageFormat);
        }
        return messageFormat.format(objects).replace(' ', ' ');
    }

    /**
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
                LOGGER.warning(String.format("Missing translation key \"%s\" in translation file %s.\n", e.getKey(), localeBundle.getLocale().toString()) + e);
            return defaultBundle.getString(key);
        }
    }

    /**
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
        LOGGER.info(String.format("Using locale %s", currentLocale.toString()));

        try {
            localeBundle = ResourceBundle.getBundle(MESSAGES, currentLocale);
        } catch (final MissingResourceException e) {
            localeBundle = NULL_BUNDLE;
        }

        try {
            customBundle = ResourceBundle.getBundle(MESSAGES, currentLocale, new FileResClassLoader(I18n.class.getClassLoader(), phenyl));
        } catch (final MissingResourceException e) {
            LOGGER.warning(String.format("Translation file of Locale %s not found!\n", customBundle.getLocale().toString()) + e);
            customBundle = NULL_BUNDLE;
        }
    }

    private static class FileResClassLoader extends ClassLoader {
        private final transient File dataFolder;

        FileResClassLoader(final ClassLoader classLoader, final Phenyl phenyl) {
            super(classLoader);
            this.dataFolder = phenyl.getDataFolder();
        }

        @Override
        public URL getResource(final String string) {
            final File file = new File(dataFolder, string);
            if (file.exists()) {
                try {
                    return file.toURI().toURL();
                }
                catch (final MalformedURLException e) {
                    LOGGER.warning(e.toString());
                }
            }
            return null;
        }

        @Override
        public InputStream getResourceAsStream(final String string) {
            final File file = new File(dataFolder, string);
            if (file.exists()) {
                try {
                    return new FileInputStream(file);
                } catch (final FileNotFoundException e) {
                    LOGGER.warning(e.toString());
                }
            }
            return null;
        }
    }

}
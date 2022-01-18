package live.turna.phenyl.utils;

import live.turna.phenyl.PhenylBase;
import live.turna.phenyl.config.PhenylConfiguration;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.File;
import java.io.IOException;

import static live.turna.phenyl.message.I18n.i18n;

/**
 * <b>LoggerUtils</b><br>
 * Provide utils for logger.
 *
 * @since 2021/12/4 14:29
 */
public class Logging extends PhenylBase {
    /**
     * Register custom filter to root logger.
     */
    private static void registerFilter() {
        Logger logger = (Logger) LogManager.getRootLogger();
        logger.addFilter(new LogFilter());
    }

    /**
     * Set the logger level.
     */
    private static void setLoggerLevel() {
        Configurator.setLevel("Phenyl", PhenylConfiguration.debug ? Level.DEBUG : Level.INFO);
    }

    /**
     * Configure the logger to log into a file.
     */
    private static void setFileLog() {
        Logger logger = (Logger) LogManager.getLogger("Phenyl");
        File file = new File(phenyl.getDataFolder(), "phenyl.log");
        if (!file.exists()) {
            try {
                if (!file.createNewFile())
                    logger.error(i18n("createLogFileFail", file.getPath()));
            } catch (IOException e) {
                logger.error(i18n("logFileFail") + e.getLocalizedMessage());
                if (PhenylConfiguration.debug) e.printStackTrace();
            }
        }
        PatternLayout layout = PatternLayout.newBuilder().withPattern("[%d{MM-dd HH:mm:ss}] %-5p %m %n").build();
        FileAppender appender = FileAppender.newBuilder()
                .withAppend(true)
                .withFileName(file.getPath())
                .setName("PhenylFileAppender")
                .setLayout(layout)
                .build();
        appender.start();
        logger.addAppender(appender);
    }

    public static void onEnable() {
        registerFilter();
        setLoggerLevel();
        setFileLog();
    }
}
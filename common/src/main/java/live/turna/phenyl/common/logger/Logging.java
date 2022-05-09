package live.turna.phenyl.common.logger;

import live.turna.phenyl.common.config.Config;
import live.turna.phenyl.common.plugin.AbstractPhenyl;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static live.turna.phenyl.common.message.I18n.i18n;

/**
 * <b>LoggerUtils</b><br>
 * Provide utils for logger.
 *
 * @since 2021/12/4 14:29
 */
public class Logging {
    private final transient AbstractPhenyl phenyl;

    public Logging(AbstractPhenyl plugin){
        phenyl=plugin;
    }

    /**
     * Register custom filter to root logger.
     */
    private void registerFilter() {
        Logger logger = (Logger) LogManager.getRootLogger();
        logger.addFilter(new LogFilter());
    }

    /**
     * Set the logger level.
     */
    private void setLoggerLevel() {
        Configurator.setLevel("Phenyl", Config.debug ? Level.DEBUG : Level.INFO);
        Configurator.setLevel("PhenylMain", Config.debug ? Level.DEBUG : Level.INFO);
        Configurator.setLevel("MIRAI", Config.debug ? Level.DEBUG : Level.INFO);
    }

    /**
     * Configure the logger to log into a file.
     */
    private void setFileLog() {
        Logger logger = (Logger) LogManager.getLogger("Phenyl");
        File file = new File(phenyl.getDir(), "phenyl.log");
        if (!file.exists()) {
            try {
                if (!file.createNewFile())
                    logger.error(i18n("createLogFileFail", file.getPath()));
            } catch (IOException e) {
                logger.error(i18n("logFileFail") + e.getLocalizedMessage());
                if (Config.debug) e.printStackTrace();
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

    private void setConsoleLog() {
        Logger logger = (Logger) LogManager.getLogger("Phenyl");
        PatternLayout layout = PatternLayout.newBuilder().withPattern("[%p][%c] %m").withCharset(StandardCharsets.UTF_8).build();
        ConsoleAppender appender = ConsoleAppender.newBuilder()
                .setLayout(layout)
                .setName("PhenylConsoleAppender")
                .build();
        logger.getAppenders().forEach(((name, oldAppender) -> {
            if (name.contains("DefaultConsole")) {
                logger.removeAppender(oldAppender);
                appender.start();
                logger.addAppender(appender);
            }
        }));
    }

    public void onEnable() {
        registerFilter();
        setConsoleLog();
        setFileLog();
        setLoggerLevel();
    }
}
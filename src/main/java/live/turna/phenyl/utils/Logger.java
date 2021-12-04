package live.turna.phenyl.utils;

import live.turna.phenyl.PhenylBase;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.Serializable;

/**
 * @author xsling
 * @version 1.0
 * @since 2021/12/4 14:29
 */
public class Logger extends PhenylBase {
    public static void setLogger(String appender_name, String package_name, Level level, @Nullable String file) {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration configuration = context.getConfiguration();
        Layout<? extends Serializable> old_layout = configuration.getAppender(appender_name).getLayout();

        configuration.getAppender(appender_name).stop();
        configuration.removeLogger(package_name);

        LoggerConfig loggerConfig = new LoggerConfig(package_name, level, false);
        if (file != null) {
            FileAppender appender = FileAppender.newBuilder()
                    .withAppend(true)
                    .withFileName(file)
                    .withImmediateFlush(true)
                    .setConfiguration(configuration)
                    .setName(appender_name)
                    .setLayout(old_layout)
                    .build();
            appender.start();
            loggerConfig.addAppender(appender, level, null);
        } else {
            ConsoleAppender appender = ConsoleAppender.newBuilder()
                    .withImmediateFlush(true)
                    .setConfiguration(configuration)
                    .setName(appender_name)
                    .setLayout(old_layout)
                    .build();
            appender.start();
            loggerConfig.addAppender(appender, level, null);
        }

        configuration.addLogger(package_name, loggerConfig);
        context.updateLoggers();
    }

}
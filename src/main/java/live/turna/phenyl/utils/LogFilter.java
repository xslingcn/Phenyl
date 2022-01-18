package live.turna.phenyl.utils;

import live.turna.phenyl.config.PhenylConfiguration;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;

/**
 * <b>LogFilter</b><br>
 * Filter and standardize the logs.
 *
 * @since 2022/1/18 22:35
 */
class LogFilter extends AbstractFilter {

    @Override
    public Result filter(LogEvent event) {
        if (event == null) {
            return Result.NEUTRAL;
        }
        if (event.getLoggerName().contains("Hikari")) {
            LogManager.getLogger("Phenyl").log(event.getLevel(), "Hikari - " + event.getMessage().getFormattedMessage());
            return Result.DENY;
        }
        if (event.getLoggerName().contains("MIRAI")) {
            switch (event.getLoggerName()) {
                case "MIRAI" -> {
                    LogManager.getLogger("Phenyl").log(event.getLevel(), "Mirai - " + event.getMessage().getFormattedMessage());
                    return Result.DENY;
                }
                case "MIRAI_NETWORK" -> {
                    if (event.getLevel().isLessSpecificThan(Level.WARN)) {
                        if (PhenylConfiguration.debug)
                            LogManager.getLogger("Phenyl").log(event.getLevel(), "MiraiNetwork - " + event.getMessage().getFormattedMessage());
                    } else
                        LogManager.getLogger("Phenyl").log(event.getLevel(), "MiraiNetwork - " + event.getMessage().getFormattedMessage());
                    return Result.DENY;
                }
                default -> {
                    return Result.NEUTRAL;
                }
            }
        }
        if (event.getLoggerName().equals("PhenylMain")) {
            LogManager.getLogger("Phenyl").log(event.getLevel(), "Main - " + event.getMessage().getFormattedMessage());
            return Result.DENY;
        }
        return Result.NEUTRAL;
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable t) {
        return Result.NEUTRAL;
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String msg, Object... params) {
        return Result.NEUTRAL;
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Object msg, Throwable t) {
        return Result.NEUTRAL;
    }
}
package live.turna.phenyl.common.logger;

import live.turna.phenyl.common.config.Config;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.filter.AbstractFilter;

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
                        if (Config.debug)
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
}
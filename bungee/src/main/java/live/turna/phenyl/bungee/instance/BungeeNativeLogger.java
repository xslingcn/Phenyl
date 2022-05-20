package live.turna.phenyl.bungee.instance;

import live.turna.phenyl.common.logger.NativeLogger;

import java.util.logging.Logger;

/**
 * <b>BungeeNativeLogger</b></br>
 *
 * @since 2022/5/21 1:28
 */
public class BungeeNativeLogger implements NativeLogger {
    private final Logger logger;

    public BungeeNativeLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void debug(String s) {
        logger.finest(s);
    }

    @Override
    public void info(String s) {
        logger.info(s);
    }

    @Override
    public void warn(String s) {
        logger.warning(s);
    }

    @Override
    public void error(String s) {
        logger.severe(s);
    }
}
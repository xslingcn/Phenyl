package live.turna.phenyl.velocity.instance;

import live.turna.phenyl.common.logger.NativeLogger;
import org.slf4j.Logger;

/**
 * <b>VelocityNativeLogger</b></br>
 *
 * @since 2022/5/21 1:26
 */
public class VelocityNativeLogger implements NativeLogger {
    private final Logger logger;

    public VelocityNativeLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void debug(String s) {
        logger.debug(s);
    }

    @Override
    public void info(String s) {
        logger.info(s);
    }

    @Override
    public void warn(String s) {
        logger.warn(s);
    }

    @Override
    public void error(String s) {
        logger.error(s);
    }
}
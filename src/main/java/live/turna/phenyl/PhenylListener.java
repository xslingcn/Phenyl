package live.turna.phenyl;

import net.md_5.bungee.api.plugin.Listener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * <b>PhenylListener</b><br> *
 *
 * @since 2021/12/4 22:20
 */
public class PhenylListener implements Listener {
    public static final Logger LOGGER = LogManager.getLogger("Phenyl");
    public static Phenyl phenyl = Phenyl.getInstance();
}
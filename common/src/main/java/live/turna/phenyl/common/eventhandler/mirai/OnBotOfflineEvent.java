package live.turna.phenyl.common.eventhandler.mirai;

import live.turna.phenyl.common.plugin.AbstractPhenyl;
import net.mamoe.mirai.event.events.BotOfflineEvent;
import org.apache.logging.log4j.Logger;

import static live.turna.phenyl.common.message.I18n.i18n;

/**
 * <b>OnBotOfflineEvent</b><br>
 * Listener on BotOfflineEvent.<br>
 * If forced offline, warn the operator to manually re-login.
 *
 * @see BotOfflineEvent
 * @since 2021/12/4 18:55
 */
public abstract class OnBotOfflineEvent<P extends AbstractPhenyl> {
    protected final transient Logger LOGGER;

    private transient String type;
    private transient Long id;

    public OnBotOfflineEvent(P plugin) {
        LOGGER = plugin.getLogger();
    }

    /**
     * @param type The offline event type.
     * @param id   The bot QQ ID.
     */
    public void fill(String type, Long id) {
        this.type = type;
        this.id = id;
    }

    public void handle() {
        if (type.equalsIgnoreCase("force"))
            LOGGER.warn(i18n("occupiedOffline", String.valueOf(id)));
        else
            LOGGER.warn(i18n("droppedOffline", String.valueOf(id), type));
    }
}
package live.turna.phenyl.listener.mirai;

import live.turna.phenyl.PhenylListener;
import live.turna.phenyl.mirai.event.CBotOfflineEvent;
import net.md_5.bungee.event.EventHandler;

import static live.turna.phenyl.message.I18n.i18n;

/**
 * <b>OnBotOfflineEvent</b><br>
 * Listener on BotOfflineEvent.
 *
 * @since 2021/12/4 18:55
 */
public class OnBotOfflineEvent extends PhenylListener {
    public OnBotOfflineEvent() {}

    @EventHandler
    public void OnBotOffline(CBotOfflineEvent event) {
        if (event.getType().equalsIgnoreCase("force"))
            LOGGER.warn(i18n("occupiedOffline", String.valueOf(event.getID())));
        else
            LOGGER.warn(i18n("droppedOffline", String.valueOf(event.getID()), event.getType()));
    }
}
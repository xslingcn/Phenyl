package live.turna.phenyl.listener.mirai;

import live.turna.phenyl.mirai.event.CBotOfflineEvent;
import net.mamoe.mirai.event.events.BotOfflineEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import static live.turna.phenyl.Phenyl.LOGGER;
import static live.turna.phenyl.message.I18n.i18n;

/**
 * <b>OnBotOfflineEvent</b><br>
 * Listener on BotOfflineEvent.<br>
 * If forced offline, log to tell operators to re-login.
 *
 * @see BotOfflineEvent
 * @since 2021/12/4 18:55
 */
public class OnBotOfflineEvent implements Listener {

    @EventHandler
    public void onBotOffline(CBotOfflineEvent event) {
        if (event.getType().equalsIgnoreCase("force"))
            LOGGER.warn(i18n("occupiedOffline", String.valueOf(event.getId())));
        else
            LOGGER.warn(i18n("droppedOffline", String.valueOf(event.getId()), event.getType()));
    }
}
package live.turna.phenyl.bungee.listener.mirai;

import live.turna.phenyl.bungee.BungeePhenyl;
import live.turna.phenyl.bungee.event.BungeeBotOfflineEvent;
import live.turna.phenyl.common.eventhandler.mirai.OnBotOfflineEvent;
import net.mamoe.mirai.event.events.BotOfflineEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * <b>BungeeOnBotOfflineEvent</b><br>
 * Listener on BotOfflineEvent.<br>
 * If forced offline, log to tell operators to re-login.
 *
 * @see BotOfflineEvent
 * @since 2021/12/4 18:55
 */
public class BungeeOnBotOfflineEvent extends OnBotOfflineEvent<BungeePhenyl> implements Listener {
    public BungeeOnBotOfflineEvent(BungeePhenyl plugin) {
        super(plugin);
    }

    @EventHandler
    public void onBotOffline(BungeeBotOfflineEvent e) {
        super.fill(e.getType(), e.getId());
        super.handle();
    }

}
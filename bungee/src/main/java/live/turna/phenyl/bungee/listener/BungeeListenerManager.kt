package live.turna.phenyl.bungee.listener;

import live.turna.phenyl.bungee.BungeePhenyl;
import live.turna.phenyl.bungee.listener.bungee.BungeeOnChatEvent;
import live.turna.phenyl.bungee.listener.bungee.BungeeOnLoginEvent;
import live.turna.phenyl.bungee.listener.bungee.BungeeOnPlayerDisconnectEvent;
import live.turna.phenyl.bungee.listener.mirai.BungeeOnBotOfflineEvent;
import live.turna.phenyl.bungee.listener.mirai.BungeeOnGroupMessageEvent;
import live.turna.phenyl.common.listener.AbstractServerListenerManager;

/**
 * <b>BungeeListenerManager</b><br>
 * *
 *
 * @since 2022/5/6 15:43
 */
public class BungeeListenerManager extends AbstractServerListenerManager<BungeePhenyl> {
    public BungeeListenerManager(BungeePhenyl plugin) {
        super(plugin);
    }

    @Override
    public void start() {
        // Mirai
        phenyl.registerListener(new BungeeOnBotOfflineEvent(phenyl));
        phenyl.registerListener(new BungeeOnGroupMessageEvent(phenyl));

        // Bungee
        phenyl.registerListener(new BungeeOnChatEvent(phenyl));
        phenyl.registerListener(new BungeeOnLoginEvent(phenyl));
        phenyl.registerListener(new BungeeOnPlayerDisconnectEvent(phenyl));
    }
}
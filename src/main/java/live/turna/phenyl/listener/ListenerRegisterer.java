package live.turna.phenyl.listener;

import live.turna.phenyl.PhenylBase;
import live.turna.phenyl.PhenylListener;
import live.turna.phenyl.config.PhenylConfiguration;
import live.turna.phenyl.listener.bungee.OnChatEvent;
import live.turna.phenyl.listener.bungee.OnLoginEvent;
import live.turna.phenyl.listener.bungee.OnPlayerDisconnectEvent;
import live.turna.phenyl.listener.mirai.OnBotOfflineEvent;
import live.turna.phenyl.listener.mirai.OnGroupMessageEvent;
import net.md_5.bungee.api.ProxyServer;

/**
 * <b>Listener</b><br>
 * Register events to Bungeecord plugin manager.
 *
 * @since 2021/12/4 22:42
 */
public class ListenerRegisterer extends PhenylBase {
    public static void registerListeners() {
        // Mirai
        ProxyServer.getInstance().getPluginManager().registerListener(phenyl, new OnBotOfflineEvent());
        ProxyServer.getInstance().getPluginManager().registerListener(phenyl, new OnGroupMessageEvent());

        // Bungee
        ProxyServer.getInstance().getPluginManager().registerListener(phenyl, new OnChatEvent());
        ProxyServer.getInstance().getPluginManager().registerListener(phenyl, new OnLoginEvent());
        ProxyServer.getInstance().getPluginManager().registerListener(phenyl, new OnPlayerDisconnectEvent());
    }

    public static void unregisterListeners() {
        ProxyServer.getInstance().getPluginManager().unregisterListeners(phenyl);
    }
}
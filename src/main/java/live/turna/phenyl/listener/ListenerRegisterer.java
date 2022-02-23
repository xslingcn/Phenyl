package live.turna.phenyl.listener;

import live.turna.phenyl.Phenyl;
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
public class ListenerRegisterer {
    public static void registerListeners() {
        // Mirai
        ProxyServer.getInstance().getPluginManager().registerListener(Phenyl.getInstance(), new OnBotOfflineEvent());
        ProxyServer.getInstance().getPluginManager().registerListener(Phenyl.getInstance(), new OnGroupMessageEvent());

        // Bungee
        ProxyServer.getInstance().getPluginManager().registerListener(Phenyl.getInstance(), new OnChatEvent());
        ProxyServer.getInstance().getPluginManager().registerListener(Phenyl.getInstance(), new OnLoginEvent());
        ProxyServer.getInstance().getPluginManager().registerListener(Phenyl.getInstance(), new OnPlayerDisconnectEvent());
    }

    public static void unregisterListeners() {
        ProxyServer.getInstance().getPluginManager().unregisterListeners(Phenyl.getInstance());
    }
}
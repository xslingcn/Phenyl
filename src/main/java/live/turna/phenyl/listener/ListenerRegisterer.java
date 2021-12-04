package live.turna.phenyl.listener;

import live.turna.phenyl.PhenylBase;
import live.turna.phenyl.listener.mirai.OnBotOfflineEvent;
import net.md_5.bungee.api.ProxyServer;

/**
 * <b>Listener</b><br> *
 *
 * @since 2021/12/4 22:42
 */
public class ListenerRegisterer extends PhenylBase {
    public static void registerListeners(){
        ProxyServer.getInstance().getPluginManager().registerListener(phenyl, new OnBotOfflineEvent());
    }
}
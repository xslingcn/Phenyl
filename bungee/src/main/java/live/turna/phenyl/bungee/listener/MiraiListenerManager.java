package live.turna.phenyl.bungee.listener;

import live.turna.phenyl.bungee.event.BungeeBotOfflineEvent;
import live.turna.phenyl.bungee.event.BungeeGroupMessageEvent;
import live.turna.phenyl.common.listener.AbstractMiraiListenerManager;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.events.BotEvent;
import net.mamoe.mirai.event.events.BotOfflineEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.md_5.bungee.api.ProxyServer;

/**
 * <b>MiraiListenerManager</b><br>
 * *
 *
 * @since 2022/5/6 15:42
 */
public class MiraiListenerManager extends AbstractMiraiListenerManager {
    @Override
    public void start(Bot bot) {
        EventChannel<BotEvent> eventChannel = bot.getEventChannel();

        BotOfflineListener = eventChannel.subscribeAlways(BotOfflineEvent.class, e -> ProxyServer.getInstance().getPluginManager().callEvent(new BungeeBotOfflineEvent(e)));
        GroupMessageListener = eventChannel.subscribeAlways(GroupMessageEvent.class, e -> ProxyServer.getInstance().getPluginManager().callEvent(new BungeeGroupMessageEvent(e)));
    }
}
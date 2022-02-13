package live.turna.phenyl.mirai;

import live.turna.phenyl.PhenylBase;
import live.turna.phenyl.mirai.event.*;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.Listener;
import net.mamoe.mirai.event.events.*;
import net.md_5.bungee.api.ProxyServer;

/**
 * <b>MiraiEvent</b><br>
 * Listen on necessary mirai events and call Bungeecord events.
 *
 * @since 2021/12/4 4:20
 */
public class MiraiEvent extends PhenylBase {
    private static Listener<BotOfflineEvent> BotOfflineListener;
    private static Listener<MemberJoinEvent> MemberJoinListener;
    private static Listener<MemberLeaveEvent> MemberLeaveListener;
    private static Listener<GroupMessageEvent> GroupMessageListener;
    private static Listener<UserMessageEvent> UserMessageListener;

    public static void listenEvents(Bot bot) {
        EventChannel<BotEvent> eventChannel = bot.getEventChannel();

        BotOfflineListener = eventChannel.subscribeAlways(BotOfflineEvent.class, e -> ProxyServer.getInstance().getPluginManager().callEvent(new CBotOfflineEvent(e)));
        MemberJoinListener = eventChannel.subscribeAlways(MemberJoinEvent.class, e -> ProxyServer.getInstance().getPluginManager().callEvent(new CMemberJoinEvent(e)));
        MemberLeaveListener = eventChannel.subscribeAlways(MemberLeaveEvent.class, e -> ProxyServer.getInstance().getPluginManager().callEvent(new CMemberLeaveEvent(e)));
        GroupMessageListener = eventChannel.subscribeAlways(GroupMessageEvent.class, e -> ProxyServer.getInstance().getPluginManager().callEvent(new CGroupMessageEvent(e)));
        UserMessageListener = eventChannel.subscribeAlways(UserMessageEvent.class, e -> ProxyServer.getInstance().getPluginManager().callEvent(new CUserMessageEvent(e)));
    }

    public static void removeListeners() {
        if (BotOfflineListener != null) BotOfflineListener.complete();
        if (MemberJoinListener != null) MemberJoinListener.complete();
        if (MemberLeaveListener != null) MemberLeaveListener.complete();
        if (GroupMessageListener != null) GroupMessageListener.complete();
        if (UserMessageListener != null) UserMessageListener.complete();
    }
}
package live.turna.phenyl.mirai;

import live.turna.phenyl.mirai.event.*;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.Listener;
import net.mamoe.mirai.event.events.*;
import net.md_5.bungee.api.ProxyServer;

/**
 * @author xsling
 * @version 1.0
 * @since 2021/12/4 4:20
 */
public class MiraiEvent {
    private transient Listener<BotOfflineEvent.Force> BotOfflineForceListener;
    private transient Listener<BotOfflineEvent.Dropped> BotOfflineDroppedListener;
    private transient Listener<BotOfflineEvent.RequireReconnect> BotOfflineRequireReconnectListener;
    private transient Listener<MemberJoinEvent> MemberJoinListener;
    private transient Listener<MemberLeaveEvent> MemberLeaveListener;
    private transient Listener<GroupMessageEvent> GroupMessageListener;
    private transient Listener<ImageUploadEvent.Succeed> ImageUploadSucceedListener;
    private transient Listener<ImageUploadEvent.Failed> ImageUploadFailedListener;
    private transient Listener<NudgeEvent> NudgeListener;
    private transient Listener<UserMessageEvent> UserMessageListener;

    private final transient EventChannel<BotEvent> eventChannel = MiraiHandler.bot.getEventChannel();

    public void listenEvents() {
        BotOfflineForceListener = eventChannel.subscribeAlways(BotOfflineEvent.Force.class, e -> ProxyServer.getInstance().getPluginManager().callEvent(new CBotOfflineEvent(e, "Force")));
        BotOfflineDroppedListener = eventChannel.subscribeAlways(BotOfflineEvent.Dropped.class, e -> ProxyServer.getInstance().getPluginManager().callEvent(new CBotOfflineEvent(e, "Dropped")));
        BotOfflineRequireReconnectListener = eventChannel.subscribeAlways(BotOfflineEvent.RequireReconnect.class, e -> ProxyServer.getInstance().getPluginManager().callEvent(new CBotOfflineEvent(e, "RequireReconnect")));
        MemberJoinListener = eventChannel.subscribeAlways(MemberJoinEvent.class, e -> ProxyServer.getInstance().getPluginManager().callEvent(new CMemberJoinEvent(e)));
        MemberLeaveListener = eventChannel.subscribeAlways(MemberLeaveEvent.class, e -> ProxyServer.getInstance().getPluginManager().callEvent(new CMemberLeaveEvent(e)));
        GroupMessageListener = eventChannel.subscribeAlways(GroupMessageEvent.class, e -> ProxyServer.getInstance().getPluginManager().callEvent(new CGroupMessageEvent(e)));
        ImageUploadSucceedListener = eventChannel.subscribeAlways(ImageUploadEvent.Succeed.class, e -> ProxyServer.getInstance().getPluginManager().callEvent(new CImageUploadEvent(e, true)));
        ImageUploadFailedListener = eventChannel.subscribeAlways(ImageUploadEvent.Failed.class, e -> ProxyServer.getInstance().getPluginManager().callEvent(new CImageUploadEvent(e, false)));
        NudgeListener = eventChannel.subscribeAlways(NudgeEvent.class, e -> ProxyServer.getInstance().getPluginManager().callEvent(new CNudgeEvent(e)));
        UserMessageListener = eventChannel.subscribeAlways(UserMessageEvent.class, e -> ProxyServer.getInstance().getPluginManager().callEvent(new CUserMessageEvent(e)));

    }

    public void removeListeners() {
        BotOfflineForceListener.complete();
        BotOfflineDroppedListener.complete();
        BotOfflineRequireReconnectListener.complete();
        MemberJoinListener.complete();
        MemberLeaveListener.complete();
        GroupMessageListener.complete();
        ImageUploadSucceedListener.complete();
        ImageUploadFailedListener.complete();
        NudgeListener.complete();
        UserMessageListener.complete();
    }


}
package live.turna.phenyl.common.listener;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.Listener;
import net.mamoe.mirai.event.events.*;

/**
 * <b>AbstractMiraiListenerManager</b><br>
 * Listen on necessary mirai events and call Bungeecord events.
 *
 * @since 2021/12/4 4:20
 */
public abstract class AbstractMiraiListenerManager {
    protected Listener<BotOfflineEvent> BotOfflineListener;
    protected Listener<MemberJoinEvent> MemberJoinListener;
    protected Listener<MemberLeaveEvent> MemberLeaveListener;
    protected Listener<GroupMessageEvent> GroupMessageListener;
    protected Listener<UserMessageEvent> UserMessageListener;

    abstract public void start(Bot bot);

    public void end() {
        if (BotOfflineListener != null) BotOfflineListener.complete();
        if (MemberJoinListener != null) MemberJoinListener.complete();
        if (MemberLeaveListener != null) MemberLeaveListener.complete();
        if (GroupMessageListener != null) GroupMessageListener.complete();
        if (UserMessageListener != null) UserMessageListener.complete();
    }
}
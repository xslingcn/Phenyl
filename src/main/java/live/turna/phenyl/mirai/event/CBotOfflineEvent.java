package live.turna.phenyl.mirai.event;

import net.mamoe.mirai.event.events.BotOfflineEvent;
import net.md_5.bungee.api.plugin.Event;

/**
 * @author xsling
 * @version 1.0
 * @since 2021/12/4 4:19
 */
public class CBotOfflineEvent extends Event {
    private final BotOfflineEvent event;
    private final String type;

    public CBotOfflineEvent(BotOfflineEvent event) {
        this.event = event;
        this.type = event.getClass().toString().split("\\$")[1];
    }
}
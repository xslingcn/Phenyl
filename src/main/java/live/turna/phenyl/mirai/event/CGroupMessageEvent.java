package live.turna.phenyl.mirai.event;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.md_5.bungee.api.plugin.Event;

/**
 * @author xsling
 * @version 1.0
 * @since 2021/12/4 4:59
 */
public class CGroupMessageEvent extends Event {
    private GroupMessageEvent event;

    public CGroupMessageEvent(GroupMessageEvent event) {
        this.event = event;
    }

}
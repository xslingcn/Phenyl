package live.turna.phenyl.mirai.event;

import net.mamoe.mirai.event.events.GroupTempMessageEvent;
import net.md_5.bungee.api.plugin.Event;

/**
 * <b>CGroupTempMessageEvent</b><br>
 *
 * @since 2021/12/5 1:23
 */
public class CGroupTempMessageEvent extends Event {
    private GroupTempMessageEvent event;

    public CGroupTempMessageEvent(GroupTempMessageEvent event) {
        this.event = event;
    }
}
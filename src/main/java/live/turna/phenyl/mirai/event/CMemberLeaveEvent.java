package live.turna.phenyl.mirai.event;

import net.mamoe.mirai.event.events.MemberLeaveEvent;
import net.md_5.bungee.api.plugin.Event;

/**
 * @author xsling
 * @version 1.0
 * @since 2021/12/4 4:59
 */
public class CMemberLeaveEvent extends Event {
    private final MemberLeaveEvent event;

    public CMemberLeaveEvent(MemberLeaveEvent event) {
        this.event = event;
    }
}
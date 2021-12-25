package live.turna.phenyl.mirai.event;

import net.mamoe.mirai.event.events.MemberJoinEvent;
import net.md_5.bungee.api.plugin.Event;

/**
 * <b>CMemberJoinEvent</b><br>
 *
 * @since 2021/12/4 4:59
 */
public class CMemberJoinEvent extends Event {
    private final MemberJoinEvent event;

    public CMemberJoinEvent(MemberJoinEvent event) {
        this.event = event;
    }

}
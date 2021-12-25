package live.turna.phenyl.mirai.event;

import net.mamoe.mirai.event.events.NudgeEvent;
import net.md_5.bungee.api.plugin.Event;

/**
 * <b>CNudgeEvent</b><br>
 *
 * @since 2021/12/4 4:59
 */
public class CNudgeEvent extends Event {
    private final NudgeEvent event;

    public CNudgeEvent(NudgeEvent event) {
        this.event = event;
    }
}
package live.turna.phenyl.mirai.event;

import net.mamoe.mirai.event.events.UserMessageEvent;
import net.md_5.bungee.api.plugin.Event;

/**
 * <b>CUserMessageEvent</b><br>
 *
 * @since 2021/12/4 5:00
 */
public class CUserMessageEvent extends Event {
    private final UserMessageEvent event;

    public CUserMessageEvent(UserMessageEvent event) {
        this.event = event;
    }
}
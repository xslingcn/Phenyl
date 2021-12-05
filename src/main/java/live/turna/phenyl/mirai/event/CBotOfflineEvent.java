package live.turna.phenyl.mirai.event;

import net.mamoe.mirai.event.events.BotOfflineEvent;
import net.md_5.bungee.api.plugin.Event;

/**
 * <b>CBotOfflineEvent</b><br>
 * BotOfflineEvent class.
 *
 * @since 2021/12/4 4:19
 */
public class CBotOfflineEvent extends Event {
    private final BotOfflineEvent event;
    private final String type;

    public CBotOfflineEvent(BotOfflineEvent event) {
        this.event = event;
        this.type = event.getClass().toString().split("\\$")[1];
    }

    /**
     * Get the type of dropping cause.<br>
     * Possible results:<br>
     * {@code Active}, {@code Force}, {@code MsfOffline}, {@code Dropped}, {@code RequireReconnect}.
     *
     * @return The type.
     * @see net.mamoe.mirai.event.events.BotOfflineEvent
     */
    public String getType() {
        return type;
    }

    /**
     * Get event bot QQ ID.
     *
     * @return The ID.
     */
    public Long getID() {
        return event.getBot().getId();
    }
}
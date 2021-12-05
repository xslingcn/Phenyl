package live.turna.phenyl.mirai.event;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.md_5.bungee.api.plugin.Event;

/**
 * <b>CGroupMessageEvent</b><br>
 * GroupMessageEvent class.
 *
 * @since 2021/12/4 4:59
 */
public class CGroupMessageEvent extends Event {
    private final GroupMessageEvent event;

    public CGroupMessageEvent(GroupMessageEvent event) {
        this.event = event;
    }

    /**
     * Get the group object.
     *
     * @return Mirai group object.
     */
    public Group getGroup() {
        return event.getGroup();
    }

    /**
     * Get the group ID.
     *
     * @return The group ID.
     */
    public Long getGroupID() {
        return event.getGroup().getId();
    }

    /**
     * Get the message sender's QQ ID.
     *
     * @return The sender's QQ ID.
     */
    public Long getSenderID() {
        return event.getSender().getId();
    }

    /**
     * Get the message sender's in-group name card.
     *
     * @return The sender's name card.
     */
    public String getSenderNameCard() {
        return event.getSender().getNameCard();
    }

    /**
     * Get the message object.
     *
     * @return Mirai message object.
     */
    public MessageChain getMessage() {
        return event.getMessage();
    }

    /**
     * Get the message plaint text to string.
     *
     * @return The message string.
     * @see Message#contentToString()
     */
    public String getMessageString() {
        return event.getMessage().contentToString();
    }

    /**
     * Get the message serialized to {@link MiraiCode}.
     *
     * @return The serialized string.
     */
    public String getMessageMiraiCode() {
        return event.getMessage().serializeToMiraiCode();
    }
}
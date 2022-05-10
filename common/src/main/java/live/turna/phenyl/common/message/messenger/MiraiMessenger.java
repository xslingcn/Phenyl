package live.turna.phenyl.common.message.messenger;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.MessageChain;

import java.awt.image.BufferedImage;
import java.util.NoSuchElementException;

public interface MiraiMessenger {
    /**
     * Send message to a certain group
     *
     * @param group   The target group.
     * @param message The message.
     */
    void sendGroup(Group group, String message);

    /**
     * Send message to a certain group
     *
     * @param group   The target group.
     * @param message The message.
     */
    void sendGroup(Group group, MessageChain message);

    /**
     * Send message to ALL enabled groups.
     *
     * @param message The message.
     */
    void sendAllGroup(String message);

    /**
     * Send message to ALL enabled groups.
     *
     * @param message The message.
     */
    void sendAllGroup(MessageChain message);

    /**
     * Send an image to a certain group.
     *
     * @param group The target group.
     * @param image The image.
     */
    void sendImage(Group group, BufferedImage image);

    /**
     * Send an image to ALL enabled groups.
     *
     * @param image The image.
     * @throws NoSuchElementException Failed getting a group.
     */
    void sendImageToAll(BufferedImage image);
}

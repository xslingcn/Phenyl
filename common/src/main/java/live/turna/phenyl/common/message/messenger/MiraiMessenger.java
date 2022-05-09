package live.turna.phenyl.common.message.messenger;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.MessageChain;

import java.awt.image.BufferedImage;

public interface MiraiMessenger {
    void sendGroup(Group group, String message);

    void sendGroup(Group group, MessageChain message);

    void sendAllGroup(String message);

    void sendAllGroup(MessageChain message);

    void sendImage(Group group, BufferedImage image);

    void sendImageToAll(BufferedImage image);
}

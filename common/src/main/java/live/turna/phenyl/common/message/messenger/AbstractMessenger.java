package live.turna.phenyl.common.message.messenger;

import live.turna.phenyl.common.config.Config;
import live.turna.phenyl.common.instance.PSender;
import live.turna.phenyl.common.plugin.AbstractPhenyl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.utils.ExternalResource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * <b>AbstractMessenger</b><br>
 * Abstract messenger, providing methods to send messages to server or QQ group.
 *
 * @since 2022/4/11 10:45
 */
public abstract class AbstractMessenger<P extends AbstractPhenyl> implements ServerMessenger, MiraiMessenger {
    private static final String ALL_CODES = "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx";

    protected final transient P phenyl;

    public AbstractMessenger(P plugin) {
        phenyl = plugin;
    }

    /**
     * Translate the color code used in locale files(&) to the one applies for Minecraft(§).
     *
     * @param message The message including the color code.
     * @return A translated String.
     */
    public static String altColor(String message) {
        char[] c = message.toCharArray();
        for (int i = 0; i < c.length - 1; i++) {
            if (c[i] == '&' && ALL_CODES.indexOf(c[i + 1]) > -1) {
                c[i] = '\u00A7';
                c[i + 1] = Character.toLowerCase(c[i + 1]);
            }
        }
        return String.valueOf(c);
    }

    public void sendGroup(Group group, String message) {
        group.sendMessage(message);
    }

    public void sendGroup(Group group, MessageChain message) {
        group.sendMessage(message);
    }

    public void sendAllGroup(String message) {
        sendAllGroup(new MessageChainBuilder().append(message).build());
    }

    public void sendAllGroup(MessageChain message) throws NoSuchElementException {
        for (Long id : Config.enabled_groups) {
            try {
                phenyl.getMirai().getBot().getGroupOrFail(id).sendMessage(message);
            } catch (NoSuchElementException e) {
                throw new NoSuchElementException(String.valueOf(id));
            }
        }
    }

    public void sendImage(Group group, BufferedImage image) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", stream);
        } catch (IOException e) {
            if (Config.debug) e.printStackTrace();
        }
        ExternalResource resource = ExternalResource.Companion.create(stream.toByteArray());
        try {
            Image img = ExternalResource.uploadAsImage(resource, group);
            MessageChain message = new MessageChainBuilder().append(img).build();
            group.sendMessage(message);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException(String.valueOf(group.getId()));
        }
        try {
            resource.close();
        } catch (IOException e) {
            if (Config.debug) e.printStackTrace();
        }
    }

    public void sendImageToAll(BufferedImage image) throws NoSuchElementException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", stream);
        } catch (IOException e) {
            if (Config.debug) e.printStackTrace();
        }
        ExternalResource resource = ExternalResource.Companion.create(stream.toByteArray());
        for (Long id : Config.enabled_groups) {
            try {
                Group group = phenyl.getMirai().getBot().getGroupOrFail(id);
                Image img = ExternalResource.uploadAsImage(resource, group);
                MessageChain message = new MessageChainBuilder().append(img).build();
                group.sendMessage(message);
            } catch (NoSuchElementException e) {
                throw new NoSuchElementException(String.valueOf(id));
            }
        }
        try {
            resource.close();
        } catch (IOException e) {
            if (Config.debug) e.printStackTrace();
        }
    }

    public void sendPlayer(String message, PSender player) {
        TextComponent result = Component.text(altColor("&7[Phenyl] " + message));
        player.sendMessage(result);
    }

    public void sendPlayer(Component message, PSender player) {
        TextComponent result = Component
                .text(altColor("&7[Phenyl] "))
                .append(message);
        player.sendMessage(result);
    }

    public void sendAllServer(String message) {
        TextComponent result = Component.text(altColor(message));
        sendAllServer(result);
    }
}
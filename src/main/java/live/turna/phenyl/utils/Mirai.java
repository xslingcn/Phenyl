package live.turna.phenyl.utils;

import live.turna.phenyl.Phenyl;
import live.turna.phenyl.config.PhenylConfiguration;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.utils.BotConfiguration;
import net.mamoe.mirai.utils.ExternalResource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.NoSuchElementException;

import static live.turna.phenyl.Phenyl.LOGGER;
import static live.turna.phenyl.message.I18n.i18n;

/**
 * <b>MiraiUtils</b><br>
 * Provide utils for Mirai.
 *
 * @since 2021/12/3 23:44
 */
public class Mirai {

    /**
     * Digest QQ password.
     *
     * @param user_pass Raw password.
     * @return Digested password.
     * @throws NoSuchAlgorithmException No MD5 algorithm
     */
    public static byte[] md5Digest(String user_pass) throws NoSuchAlgorithmException {
        byte[] pass;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(user_pass.getBytes(StandardCharsets.UTF_8));
            pass = digest.digest();
            return pass;
        } catch (NoSuchElementException e) {
            throw new NoSuchAlgorithmException(i18n("digestFail") + e.getLocalizedMessage());
        }
    }

    /**
     * Check the existence of mirai working directory and create it if not exists.
     *
     * @param workingDir The mirai working directory File.
     * @return The created or already-existed workingDir.
     * @throws IOException Fails to create the directory.
     */
    public static File checkMiraiDir(File workingDir) throws IOException {
        if (!workingDir.exists()) {
            if (!workingDir.mkdir()) {
                throw new IOException(i18n("createMiraiDirFail"));
            }
            LOGGER.info(i18n("createMiraiDir"));
        }
        return workingDir;
    }

    /**
     * Match which protocol is set in config.
     *
     * @param proString Protocol config string.
     * @return The matching protocol.
     * @throws IllegalArgumentException None protocol is matched.
     */
    public static BotConfiguration.MiraiProtocol matchProtocol(String proString) throws IllegalArgumentException {
        BotConfiguration.MiraiProtocol protocol;
        if (proString.equalsIgnoreCase("ANDROID_PHONE")) {
            protocol = BotConfiguration.MiraiProtocol.ANDROID_PHONE;
        } else if (proString.equalsIgnoreCase("ANDROID_PAD")) {
            protocol = BotConfiguration.MiraiProtocol.ANDROID_PAD;
        } else if (proString.equalsIgnoreCase("ANDROID_WATCH")) {
            protocol = BotConfiguration.MiraiProtocol.ANDROID_WATCH;
        } else throw new IllegalArgumentException(i18n("matchProtocolFail"));
        return protocol;
    }

    /**
     * Send group message.
     *
     * @param group   The group to send to.
     * @param message The message in plain string type.
     */
    public static void sendGroup(Group group, String message) {
        group.sendMessage(message);
    }

    /**
     * Send group message.
     *
     * @param group   The group to send to.
     * @param message The message in {@link MessageChain} type.
     */
    public static void sendGroup(Group group, MessageChain message) {
        group.sendMessage(message);
    }

    /**
     * Send all group the message.
     *
     * @param message The message in plain string type.
     */
    public static void sendGroup(String message) throws NoSuchElementException {
        sendGroup(new MessageChainBuilder().append(message).build());
    }

    /**
     * Send all group the message.
     *
     * @param message The message in {@link MessageChain} type.
     */
    public static void sendGroup(MessageChain message) throws NoSuchElementException {
        for (Long id : PhenylConfiguration.enabled_groups) {
            try {
                Phenyl.getInstance().getMirai().getBot().getGroupOrFail(id).sendMessage(message);
            } catch (NoSuchElementException e) {
                throw new NoSuchElementException(String.valueOf(id));
            }
        }
    }

    /**
     * Send an image message.
     *
     * @param image The image to be sent.
     * @throws NoSuchElementException Target group not found.
     */
    public static void sendImage(BufferedImage image) throws NoSuchElementException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", stream);
        } catch (IOException e) {
            if (PhenylConfiguration.debug) e.printStackTrace();
        }
        ExternalResource resource = ExternalResource.Companion.create(stream.toByteArray());
        for (Long id : PhenylConfiguration.enabled_groups) {
            try {
                Group group = Phenyl.getInstance().getMirai().getBot().getGroupOrFail(id);
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
            if (PhenylConfiguration.debug) e.printStackTrace();
        }
    }
}
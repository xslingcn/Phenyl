package live.turna.phenyl.common.utils;

import live.turna.phenyl.common.config.Config;
import live.turna.phenyl.common.plugin.AbstractPhenyl;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.MessageReceipt;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.utils.BotConfiguration;
import net.mamoe.mirai.utils.ExternalResource;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import static live.turna.phenyl.common.message.I18n.i18n;

/**
 * <b>MiraiUtils</b><br>
 * Provide utils for MiraiUtils.
 *
 * @since 2021/12/3 23:44
 */
public class MiraiUtils {
    private final transient AbstractPhenyl phenyl;
    private final transient Logger LOGGER;

    public MiraiUtils(AbstractPhenyl plugin) {
        phenyl = plugin;
        LOGGER = phenyl.getLogger();
    }

    /**
     * Digest QQ password.
     *
     * @param user_pass Raw password.
     * @return Digested password.
     * @throws NoSuchAlgorithmException No MD5 algorithm
     */
    public byte[] md5Digest(String user_pass) throws NoSuchAlgorithmException {
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
    public File checkMiraiDir(File workingDir) throws IOException {
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
    public BotConfiguration.MiraiProtocol matchProtocol(String proString) throws IllegalArgumentException {
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

    public String getUserNameOrNameCardOrNick(Long groupId, Long senderId) throws NoSuchElementException {
        String userName = null;
        if (Config.forward_mode.equals("command") || Config.forward_mode.equals("bind"))
            userName = phenyl.getStorage().getBinding(senderId).mcname();

        if (userName == null) {
            try {
                Member from = phenyl.getMirai().getBot().getGroupOrFail(groupId).getOrFail(senderId);
                userName = from.getNameCard().isEmpty() ? from.getNick() : from.getNameCard();
            } catch (NoSuchElementException e) {
                throw new NoSuchElementException(String.valueOf(groupId));
            }
        }
        return userName;
    }

    /**
     * Send group message.
     *
     * @param group   The group to send to.
     * @param message The message in plain string type.
     */
    public MessageReceipt<Group> sendGroup(Group group, String message) {
        return group.sendMessage(message);
    }

    /**
     * Send group message.
     *
     * @param group   The group to send to.
     * @param message The message in {@link MessageChain} type.
     */
    public MessageReceipt<Group> sendGroup(Group group, MessageChain message) {
        return group.sendMessage(message);
    }

    /**
     * Send all group the message.
     *
     * @param message The message in plain string type.
     */
    public ArrayList<MessageReceipt<Group>> sendAllGroup(String message) throws NoSuchElementException {
        return sendAllGroup(new MessageChainBuilder().append(message).build());
    }

    public ArrayList<MessageReceipt<Group>> sendAllGroup(String message, ArrayList<MessageReceipt<Group>> referenceReceipts) throws NoSuchElementException {
        return sendAllGroup(new MessageChainBuilder().append(message).build(), referenceReceipts);
    }

    /**
     * Send all group the message.
     *
     * @param message The message in {@link MessageChain} type.
     */
    public ArrayList<MessageReceipt<Group>> sendAllGroup(MessageChain message) throws NoSuchElementException {
        return sendAllGroup(message, null);
    }

    public ArrayList<MessageReceipt<Group>> sendAllGroup(MessageChain message, ArrayList<MessageReceipt<Group>> referenceReceipts) throws NoSuchElementException {
        ArrayList<MessageReceipt<Group>> receipts = new ArrayList<>();
        for (Long id : Config.enabled_groups) {
            try {
                Group group = phenyl.getMirai().getBot().getGroupOrFail(id);
                MessageChainBuilder msg = new MessageChainBuilder().append(message);
                if (referenceReceipts != null)
                    referenceReceipts.stream().filter(r -> r.getTarget().equals(group)).forEach(r -> msg.append(r.quote()));
                receipts.add(group.sendMessage(msg.build()));
            } catch (NoSuchElementException e) {
                throw new NoSuchElementException(String.valueOf(id));
            }
        }
        return receipts;
    }

    /**
     * Send an image message.
     *
     * @param image The image to be sent.
     * @throws NoSuchElementException Target group not found.
     */
    public ArrayList<MessageReceipt<Group>> sendImage(BufferedImage image, ArrayList<MessageReceipt<Group>> referenceReceipts) throws NoSuchElementException {
        ArrayList<MessageReceipt<Group>> receipts = new ArrayList<>();
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
                MessageChainBuilder message = new MessageChainBuilder().append(img);
                if (referenceReceipts != null)
                    referenceReceipts.stream().filter(r -> r.getTarget().equals(group)).forEach(r -> message.append(r.quote()));
                receipts.add(group.sendMessage(message.build()));
            } catch (NoSuchElementException e) {
                throw new NoSuchElementException(String.valueOf(id));
            }
        }
        try {
            resource.close();
        } catch (IOException e) {
            if (Config.debug) e.printStackTrace();
        }
        return receipts;
    }

    public ArrayList<MessageReceipt<Group>> sendImage(BufferedImage image) throws NoSuchElementException {
        return sendImage(image, null);
    }
}
package live.turna.phenyl.utils;

import live.turna.phenyl.PhenylBase;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.utils.BotConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static live.turna.phenyl.message.I18n.i18n;

/**
 * <b>MiraiUtils</b><br>
 * Provide utils for Mirai.
 *
 * @since 2021/12/3 23:44
 */
public class Mirai extends PhenylBase {

    /**
     * Digest QQ password.
     *
     * @param user_pass Raw password.
     * @return Digested password.
     * @throws NoSuchAlgorithmException No MD5 algorithm
     */
    public static byte[] md5Digest(String user_pass) throws NoSuchAlgorithmException {
        byte[] pass;
        MessageDigest digest = MessageDigest.getInstance("MD5");
        digest.update(user_pass.getBytes(StandardCharsets.UTF_8));
        pass = digest.digest();
        return pass;
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
                throw new IOException();
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
        } else throw new IllegalArgumentException();
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
}
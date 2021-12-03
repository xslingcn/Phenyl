package live.turna.phenyl.utils;

import live.turna.phenyl.PhenylBase;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.utils.BotConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static live.turna.phenyl.message.I18n.i18n;

/**
 * @author xsling
 * @version 1.0
 * @since 2021/12/3 23:44
 */
public class Mirai extends PhenylBase {

    /**
     * Digest QQ password.
     * @param user_pass Raw password.
     * @return Digested password.
     * @throws NoSuchAlgorithmException No MD5 algorithm
     */
    public static String md5Digest(String user_pass) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        digest.update(user_pass.getBytes(StandardCharsets.UTF_8));
        user_pass = Arrays.toString(digest.digest());
        return user_pass;
    }

    /**
     * Check the existence of mirai working directory and create it if not exists.
     * @param workingDir The mirai working directory File.
     * @return The created or already-existed workingDir.
     * @throws IOException Fails to create the directory.
     */
    public static File checkMiraiDir (File workingDir) throws IOException{
        if(!workingDir.exists()){
            if(!workingDir.mkdir()){
                throw new IOException();
            }
            LOGGER.info(i18n("createMiraiDir"));
        }
        return workingDir;
    }

    /**
     * Match which protocol is set in config.
     * @param proString Protocol config string.
     * @return The matching protocol.
     * @throws IllegalArgumentException None protocol is matched.
     */
    public static BotConfiguration.MiraiProtocol matchProtocol (String proString) throws IllegalArgumentException{
        BotConfiguration.MiraiProtocol protocol;
        if(proString.equalsIgnoreCase("ANDROID_PHONE")){
            protocol = BotConfiguration.MiraiProtocol.ANDROID_PHONE;
        }
        else if(proString.equalsIgnoreCase("ANDROID_PAD")){
            protocol = BotConfiguration.MiraiProtocol.ANDROID_PAD;
        }
        else if(proString.equalsIgnoreCase("AANDROID_WATCH")){
            protocol = BotConfiguration.MiraiProtocol.ANDROID_WATCH;
        }
        else throw new IllegalArgumentException();
        return protocol;
    }

}
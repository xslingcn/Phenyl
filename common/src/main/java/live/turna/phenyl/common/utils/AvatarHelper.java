package live.turna.phenyl.common.utils;

import live.turna.phenyl.common.config.Config;
import live.turna.phenyl.common.plugin.AbstractPhenyl;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import static live.turna.phenyl.common.message.I18n.i18n;

/**
 * <b>AvatarHelper</b><br>
 * Utils work for avatar.
 *
 * @since 2021/12/26 19:20
 */
public class AvatarHelper {
    private final transient AbstractPhenyl phenyl;
    private final transient Logger LOGGER;
    public AvatarHelper(AbstractPhenyl plugin){
        phenyl=plugin;
        LOGGER=phenyl.getLogger();
    }
    /**
     * Create the avatar file if not exists, update it otherwise.
     *
     * @param uuid The player's Minecraft UUID.
     * @return The avatar file instance.
     * @throws IOException File operation failed.
     */
    private File createAvatarFile(String uuid) throws IOException {
        File storageDir = new File(phenyl.getDir(), "storage");
        if (!storageDir.exists()) {
            if (!storageDir.mkdir()) {
                throw new IOException(i18n("createAvatarFail", storageDir.toString()));
            }
        }
        File playerDir = new File(storageDir, uuid);
        if (!playerDir.exists()) {
            if (!playerDir.mkdir()) {
                throw new IOException(i18n("createAvatarFail", playerDir.toString()));
            }
        }
        File playerAvatar = new File(playerDir, "avatar.png");
        if (playerAvatar.exists()) {
            if (!playerAvatar.delete())
                throw new IOException(i18n("createAvatarFail", playerAvatar.toString()));
        }
        try {
            if (!playerAvatar.createNewFile())
                throw new IOException(i18n("createAvatarFail", playerAvatar.toString()));
        } catch (IOException e) {
            if (Config.debug) e.printStackTrace();
        }
        return playerAvatar;
    }

    /**
     * Download the avatar from remote.
     *
     * @param uuid The player's Minecraft UUID.
     * @return True - succeeded; False - caught any exceptions.
     */
    public boolean downloadAvatar(String uuid) {
        try {
            BufferedImage avatar = new BufferedImage(Config.avatar_size, Config.avatar_size, BufferedImage.TYPE_INT_RGB);
            URL url = new URL(Config.crafatar_url + "/avatars/" + uuid);

            Graphics2D graphics2D = avatar.createGraphics();
            graphics2D.drawImage(ImageIO.read(url), 0, 0, Config.avatar_size, Config.avatar_size, null);
            graphics2D.dispose();

            File avatarFile = createAvatarFile(uuid);
            ImageIO.write(avatar, "png", avatarFile);
            return true;
        } catch (Exception e) {
            LOGGER.error(i18n("getAvatarFail", e.getLocalizedMessage()));
            if (Config.debug) e.printStackTrace();
        }
        return false;
    }

    /**
     * Get a player's avatar file by uuid.
     *
     * @param uuid The player's Minecraft UUID.
     * @return The avatar file instance. Notice that it can be null.
     */
    public File getAvatar(String uuid) {
        return new File(phenyl.getDir() + "/storage/" + uuid + "/avatar.png");
    }
}
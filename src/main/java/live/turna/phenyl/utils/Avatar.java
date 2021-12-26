package live.turna.phenyl.utils;

import live.turna.phenyl.PhenylBase;
import live.turna.phenyl.config.PhenylConfiguration;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import static live.turna.phenyl.message.I18n.i18n;

/**
 * <b>Avatar</b><br>
 * Utils work for avatar.
 *
 * @since 2021/12/26 19:20
 */
public class Avatar extends PhenylBase {
    /**
     * Create the avatar file if not exists, update it otherwise.
     *
     * @param uuid The player's Minecraft UUID.
     * @return The avatar file instance.
     * @throws IOException File operation failed.
     */
    private static File createAvatarFile(String uuid) throws IOException {
        File storageDir = new File(phenyl.getDataFolder(), "storage");
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
            if (PhenylConfiguration.debug) e.printStackTrace();
        }
        return playerAvatar;
    }

    /**
     * Download the avatar from remote.
     *
     * @param uuid The player's Minecraft UUID.
     * @return True - succeeded; False - caught any exceptions.
     */
    public static boolean downloadAvatar(String uuid) {
        try {
            BufferedImage avatar = new BufferedImage(PhenylConfiguration.avatar_size, PhenylConfiguration.avatar_size, BufferedImage.TYPE_INT_RGB);
            URL url = new URL(PhenylConfiguration.crafatar_url + "/avatars/" + uuid);

            Graphics2D graphics2D = avatar.createGraphics();
            graphics2D.drawImage(ImageIO.read(url), 0, 0, PhenylConfiguration.avatar_size, PhenylConfiguration.avatar_size, null);
            graphics2D.dispose();

            File avatarFile = createAvatarFile(uuid);
            ImageIO.write(avatar, "png", avatarFile);
            return true;
        } catch (Exception e) {
            LOGGER.error(i18n("getAvatarFail", e.getLocalizedMessage()));
            if (PhenylConfiguration.debug) e.printStackTrace();
        }
        return false;
    }

    /**
     * Get a player's avatar file by uuid.
     *
     * @param uuid The player's Minecraft UUID.
     * @return The avatar file instance. Notice that it can be null.
     */
    public static File getAvatar(String uuid) {
        return new File(phenyl.getDataFolder() + "/storage/" + uuid + "/avatar.png");
    }
}
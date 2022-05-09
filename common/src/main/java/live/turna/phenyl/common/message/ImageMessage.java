package live.turna.phenyl.common.message;


import live.turna.phenyl.common.config.Config;
import live.turna.phenyl.common.plugin.AbstractPhenyl;
import live.turna.phenyl.common.utils.AvatarHelper;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.net.URL;

import static live.turna.phenyl.common.message.I18n.i18n;

/**
 * <b>ImageMessage</b><br>
 * Produce image messages.
 *
 * @since 2021/12/27 3:17
 */
public class ImageMessage {
    private final transient AbstractPhenyl phenyl;
    private final transient Logger LOGGER;

    public ImageMessage(AbstractPhenyl plugin){
        phenyl=plugin;
        LOGGER=phenyl.getLogger();
    }
    /**
     * Draw an image message.
     *
     * @param message  The message content.
     * @param userName The player's Minecraft username.
     * @param uuid     The player's Minecraft UUID.
     * @return The output image instance.
     */
    public BufferedImage drawImageMessage(String message, String userName, String uuid) {

        // get width and height and handle message
        Graphics2D g = new BufferedImage(1, 1, 1).createGraphics();
        g.setFont(new Font(Config.font, Font.PLAIN, Config.message_size));
        FontMetrics fm = g.getFontMetrics();
        Rectangle2D rect = fm.getStringBounds("a", g);
        int width = (int) Math.floor(getWidth(userName, message, rect));
        message = handleString(message, width, rect);
        int height = (int) Math.floor(getHeight(message, rect));

        BufferedImage avatar = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = avatar.createGraphics();

        // set background color
        graphics2D.setColor(Color.DARK_GRAY);
        graphics2D.fillRect(0, 0, width, height);

        // draw avatar
        try {
            graphics2D.drawImage(ImageIO.read(new AvatarHelper(phenyl).getAvatar(uuid)), Config.overall_padding, Config.overall_padding, Config.avatar_size, Config.avatar_size, null);
        } catch (Exception e) {
            LOGGER.error(i18n("getAvatarFail", e.getLocalizedMessage()));
            e.printStackTrace();
        }

        // text settings
        graphics2D.setColor(Color.LIGHT_GRAY);
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        graphics2D.setFont(new Font(Config.font, Font.PLAIN, Config.username_size));
        FontMetrics fontMetrics = graphics2D.getFontMetrics();
        graphics2D.drawString(userName, (float) (Config.avatar_size + ((double) Config.username_avatar_margin)), (float) ((Config.avatar_size + Config.overall_padding * 2) / 2 + Config.username_offset));

        graphics2D.setFont(new Font(Config.font, Font.PLAIN, Config.message_size));
        Rectangle2D rectangle2D = fontMetrics.getStringBounds("1", graphics2D);
        double lineHeight = Config.avatar_size + Config.overall_padding * 2 + Config.message_offset;
        for (String line : message.split("\n")) {
            graphics2D.drawString(line, (float) (Config.overall_padding), (float) lineHeight);
            lineHeight += rectangle2D.getHeight();
        }
        graphics2D.dispose();
        return avatar;
    }

    /**
     * Get the image's width, which is flexibly between max_width and min_width.
     *
     * @param message The message content.
     * @param rect    The font's rectangle instance.
     * @return The proper width.
     */
    public double getWidth(String userName, String message, Rectangle2D rect) {
        String doubleRegex = "[^\\x00-\\xff]";
        message = message.replaceAll(doubleRegex, "aa");
        double userNameLength = rect.getWidth() * userName.length() + Config.avatar_size + Config.overall_padding * 3;
        double result = rect.getWidth() * message.length() + Config.overall_padding * 2;
        result = Math.max(result, userNameLength);
        result = Math.min(result, Config.message_max_width);
        result = Math.max(result, Config.message_min_width);
        return result;
    }

    /**
     * Get the image's height, plus a font's height for every new line.
     *
     * @param message The message content.
     * @param rect    The font's rectangle instance.
     * @return The proper height.
     */
    public double getHeight(String message, Rectangle2D rect) {
        double height = Config.overall_padding * 2 + Config.avatar_size;
        for (String ignored : message.split("\n"))
            height += rect.getHeight();
        return height;
    }

    /**
     * Handle the message content, calculating how many characters can be in a single line.
     *
     * @param message  The message content.
     * @param maxWidth The image message's max width.
     * @param rect     The font's rectangle instance.
     * @return The processed string.
     */
    public String handleString(String message, double maxWidth, Rectangle2D rect) {
        int maxChars = (int) Math.floor((maxWidth - Config.overall_padding) / rect.getWidth());
        return insertPeriodically(message, maxChars);
    }

    /**
     * Insert \n at the end of each line.<br>
     * Notice that double-byte characters needs an additional count.
     *
     * @param message  The message content.
     * @param maxChars The maximum count of characters in a single line.
     * @return The processed string.
     */
    public String insertPeriodically(String message, int maxChars) {
        StringBuilder builder = new StringBuilder();
        String doubleRegex = "[^\\x00-\\xff]";
        int index = 0;
        String prefix = "\n";
        for (char c : message.toCharArray()) {
            if (index >= maxChars) {
                builder.append(prefix);
                index = 0;
            }
            if (String.valueOf(c).matches(doubleRegex)) index++;
            index++;
            builder.append(c);
        }
        return builder.toString();
    }

    /**
     * Get a image from giving URL.
     *
     * @param url The remote URL.
     * @return The buffered image.
     */
    public BufferedImage getImageFromURL(String url) {
        try {
            return ImageIO.read(new URL(url));
        } catch (Exception e) {
            LOGGER.error(i18n("getImageFail", e.getLocalizedMessage()));
            if (Config.debug) e.printStackTrace();
        }
        return null;
    }
}
package live.turna.phenyl.message;

import live.turna.phenyl.PhenylBase;
import live.turna.phenyl.config.PhenylConfiguration;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static live.turna.phenyl.utils.Avatar.getAvatar;

/**
 * <b>ImageMessage</b><br>
 * *
 *
 * @since 2021/12/27 3:17
 */
public class ImageMessage extends PhenylBase {
    /**
     * Draw an image message.
     *
     * @param message  The message content.
     * @param userName The player's Minecraft username.
     * @param uuid     The player's Minecraft UUID.
     * @return The output image instance.
     */
    public static BufferedImage drawImageMessage(String message, String userName, String uuid) {

        // get width and height and handle message
        Graphics2D g = new BufferedImage(1, 1, 1).createGraphics();
        g.setFont(new Font(PhenylConfiguration.font, Font.PLAIN, PhenylConfiguration.message_size));
        FontMetrics fm = g.getFontMetrics();
        Rectangle2D rect = fm.getStringBounds("a", g);
        int width = (int) Math.floor(getWidth(message, rect));
        message = handleString(message, width, rect);
        int height = (int) Math.floor(getHeight(message, rect));

        BufferedImage avatar = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = avatar.createGraphics();

        // set background color
        graphics2D.setColor(Color.DARK_GRAY);
        graphics2D.fillRect(0, 0, width, height);

        // draw avatar
        try {
            graphics2D.drawImage(ImageIO.read(getAvatar(uuid)), PhenylConfiguration.overall_padding, PhenylConfiguration.overall_padding, PhenylConfiguration.avatar_size, PhenylConfiguration.avatar_size, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // text settings
        graphics2D.setColor(Color.LIGHT_GRAY);
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        graphics2D.setFont(new Font(PhenylConfiguration.font, Font.PLAIN, PhenylConfiguration.username_size));
        FontMetrics fontMetrics = graphics2D.getFontMetrics();
        graphics2D.drawString(userName, (float) (PhenylConfiguration.avatar_size + ((double) PhenylConfiguration.username_avatar_margin)), (float) ((PhenylConfiguration.avatar_size + PhenylConfiguration.overall_padding * 2) / 2 + PhenylConfiguration.username_offset));

        graphics2D.setFont(new Font(PhenylConfiguration.font, Font.PLAIN, PhenylConfiguration.message_size));
        Rectangle2D rectangle2D = fontMetrics.getStringBounds("1", graphics2D);
        double lineHeight = PhenylConfiguration.avatar_size + PhenylConfiguration.overall_padding * 2 + PhenylConfiguration.message_offset;
        for (String line : message.split("\n")) {
            graphics2D.drawString(line, (float) (PhenylConfiguration.overall_padding), (float) lineHeight);
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
    public static double getWidth(String message, Rectangle2D rect) {
        String doubleRegex = "[^\\x00-\\xff]";
        message = message.replaceAll(doubleRegex, "aa");
        double result = rect.getWidth() * message.length() + PhenylConfiguration.overall_padding * 2;
        result = Math.min(result, PhenylConfiguration.message_max_width);
        result = Math.max(result, PhenylConfiguration.message_min_width);
        return result;
    }

    /**
     * Get the image's height, plus a font's height for every new line.
     *
     * @param message The message content.
     * @param rect    The font's rectangle instance.
     * @return The proper height.
     */
    public static double getHeight(String message, Rectangle2D rect) {
        double height = PhenylConfiguration.overall_padding * 2 + PhenylConfiguration.avatar_size;
        for (String line : message.split("\n"))
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
    public static String handleString(String message, double maxWidth, Rectangle2D rect) {
        int maxChars = (int) Math.floor((maxWidth - PhenylConfiguration.overall_padding) / rect.getWidth());
        return insertPeriodically(message, maxChars, rect);
    }

    /**
     * Insert \n at the end of each line.<br>
     * Notice that double-byte characters needs an additional count.
     *
     * @param message  The message content.
     * @param maxChars The maximum count of characters in a single line.
     * @param rect     The font's rectangle instance.
     * @return The processed string.
     */
    public static String insertPeriodically(String message, int maxChars, Rectangle2D rect) {
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
}
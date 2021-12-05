package live.turna.phenyl.utils;

import live.turna.phenyl.PhenylBase;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * <b>MessageUtils</b><br>
 * Utils for message sending.
 *
 * @since 2021/12/3 20:27
 */
public class Message extends PhenylBase {

    /**
     * Make messages with color codes translatable.
     *
     * @param message The message with color codes.
     * @return String Translated messages.
     */
    public static String altColor(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Easily send messages to players.
     *
     * @param message The message needs to be sent.
     * @param sender  The receiver of the message.
     */
    public static void sendMessage(String message, CommandSender sender) {
        TextComponent result = new TextComponent(altColor(message));
        sender.sendMessage(result);
    }

}
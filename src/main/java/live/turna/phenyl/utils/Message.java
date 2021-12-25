package live.turna.phenyl.utils;

import live.turna.phenyl.PhenylBase;
import live.turna.phenyl.config.PhenylConfiguration;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;

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

    public static void broadcastMessage(String message) {
        TextComponent result = new TextComponent(altColor(message));
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (PhenylConfiguration.enabled_servers.contains(player.getServer().getInfo().getName())) {
                player.sendMessage(result);
            }
        }
    }

    public static void broadcastMessage(String message, Server[] exclude) {
        TextComponent result = new TextComponent(altColor(message));
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (PhenylConfiguration.enabled_servers.contains(player.getServer().getInfo().getName())) {
                for (Server server : exclude)
                    if (server != player.getServer())
                        player.sendMessage(result);
            }
        }
    }

    public static void broadcastMessage(BaseComponent[] message) {
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (PhenylConfiguration.enabled_servers.contains(player.getServer().getInfo().getName())) {
                player.sendMessage(message);
            }
        }
    }

    public static String getServerName(Server server) {
        String serverName = server.getInfo().getName();
        String alia = PhenylConfiguration.server_alias.get(serverName);
        if (alia == null || alia.isEmpty()) return serverName;
        return alia;
    }
}
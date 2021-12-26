package live.turna.phenyl.utils;

import live.turna.phenyl.Phenyl;
import live.turna.phenyl.PhenylBase;
import live.turna.phenyl.config.PhenylConfiguration;
import live.turna.phenyl.database.Player;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;

import java.util.concurrent.atomic.AtomicReference;

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
        TextComponent result = new TextComponent(altColor("&7[Phenyl] " + message));
        sender.sendMessage(result);
    }

    /**
     * Send message to every player in enabled servers.
     *
     * @param message The message content.
     */
    public static void broadcastMessage(String message) {
        TextComponent result = new TextComponent(altColor(message));
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (PhenylConfiguration.enabled_servers.contains(player.getServer().getInfo().getName())) {
                player.sendMessage(result);
            }
        }
    }

    /**
     * Send message to every player in enabled and not excluded servers.
     *
     * @param message The message content.
     * @param exclude Excluded servers.
     */
    public static void broadcastMessage(String message, Server[] exclude) {
        TextComponent result = new TextComponent(altColor(message));
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (PhenylConfiguration.enabled_servers.contains(player.getServer().getInfo().getName())) {
                for (Server server : exclude) {
                    if (server != player.getServer()) {
                        if (getNoMessage(player.getUniqueId().toString()).uuid() != null) continue;
                        player.sendMessage(result);
                    }
                }
            }
        }
    }

    /**
     * Send message to every player in enabled servers.
     *
     * @param message The message of {@link BaseComponent} type to be sent.
     */
    public static void broadcastMessage(BaseComponent[] message) {
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (PhenylConfiguration.enabled_servers.contains(player.getServer().getInfo().getName())) {
                if (getNoMessage(player.getUniqueId().toString()).uuid() != null) continue;
                player.sendMessage(message);
            }
        }
    }

    /**
     * Check whether the player is muted.
     *
     * @param uuid The player's Minecraft UUID.
     * @return The muted player instance if found, a player instance initialized with all null values if not.
     */
    public static Player getMuted(String uuid) {
        AtomicReference<Player> found = new AtomicReference<>(new Player(null, null, null, null));
        Phenyl.getMutedPlayer().forEach(muted -> {
            if (muted.uuid() == null) return;
            if (muted.uuid().equals(uuid)) found.set(muted);
        });
        return found.get();
    }

    /**
     * Check whether the player is nomessaged.
     *
     * @param uuid The player's Minecraft UUID.
     * @return The nomessaged player instance if found, a player instance initialized with all null values if not.
     */
    public static Player getNoMessage(String uuid) {
        AtomicReference<Player> found = new AtomicReference<>(new Player(null, null, null, null));
        Phenyl.getNoMessagePlayer().forEach(noMessaged -> {
            if (noMessaged.uuid() == null) return;
            if (noMessaged.uuid().equals(uuid)) found.set(noMessaged);
        });
        return found.get();
    }

    /**
     * Get the server's alia set in {@code server_alias}, return the server name from bungee if not found.
     *
     * @param server The server to get the name.
     * @return Server alia or server name.
     */
    public static String getServerName(Server server) {
        String serverName = server.getInfo().getName();
        String alia = PhenylConfiguration.server_alias.get(serverName);
        if (alia == null || alia.isEmpty()) return serverName;
        return alia;
    }

    public static String getServerName(ServerInfo serverInfo) {
        String serverName = serverInfo.getName();
        String alia = PhenylConfiguration.server_alias.get(serverName);
        if (alia == null || alia.isEmpty()) return serverName;
        return alia;
    }
}
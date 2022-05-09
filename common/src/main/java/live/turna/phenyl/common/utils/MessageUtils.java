package live.turna.phenyl.common.utils;

import live.turna.phenyl.common.config.Config;
import live.turna.phenyl.common.database.Player;
import live.turna.phenyl.common.plugin.AbstractPhenyl;

import java.util.concurrent.atomic.AtomicReference;

/**
 * <b>MessageUtils</b><br>
 * *
 *
 * @since 2022/4/11 16:37
 */
public class MessageUtils {
    private final transient AbstractPhenyl phenyl;

    public MessageUtils(AbstractPhenyl plugin) {
        phenyl = plugin;
    }

    /**
     * Check whether the player is muted.
     *
     * @param uuid The player's Minecraft UUID.
     * @return The muted player instance if found, a player instance initialized with all null values if not.
     */
    public Player isMuted(String uuid) {
        AtomicReference<Player> found = new AtomicReference<>(new Player(null, null, null, null));
        phenyl.getMutedPlayer().forEach(muted -> {
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
    public Player isNoMessaged(String uuid) {
        AtomicReference<Player> found = new AtomicReference<>(new Player(null, null, null, null));
        phenyl.getNoMessagePlayer().forEach(noMessaged -> {
            if (noMessaged.uuid() == null) return;
            if (noMessaged.uuid().equals(uuid)) found.set(noMessaged);
        });
        return found.get();
    }

    /**
     * Get the server's alia set in {@code server_alias}, return the server name from bungee if not found.
     *
     * @param serverName The server to get the name.
     * @return String alia or server name.
     */
    public String getServerName(String serverName) {
        String alia = Config.server_alias.get(serverName);
        if (alia == null || alia.isEmpty()) return serverName;
        return alia;
    }
}
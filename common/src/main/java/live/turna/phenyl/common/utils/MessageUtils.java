package live.turna.phenyl.common.utils;

import live.turna.phenyl.common.config.Config;
import live.turna.phenyl.common.database.Player;
import live.turna.phenyl.common.plugin.AbstractPhenyl;
import net.kyori.adventure.text.format.NamedTextColor;

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
     * Get a color by color code.
     *
     * @param color The code.
     * @return A color among {@link NamedTextColor}.
     */
    public static NamedTextColor getColor(char color) {
        return switch (color) {
            case '0' -> NamedTextColor.BLACK;
            case '1' -> NamedTextColor.DARK_BLUE;
            case '2' -> NamedTextColor.DARK_GREEN;
            case '3' -> NamedTextColor.DARK_AQUA;
            case '4' -> NamedTextColor.DARK_RED;
            case '5' -> NamedTextColor.DARK_PURPLE;
            case '6' -> NamedTextColor.GOLD;
            case '7' -> NamedTextColor.GRAY;
            case '8' -> NamedTextColor.DARK_GRAY;
            case '9' -> NamedTextColor.BLUE;
            case 'a' -> NamedTextColor.GREEN;
            case 'b' -> NamedTextColor.AQUA;
            case 'c' -> NamedTextColor.RED;
            case 'd' -> NamedTextColor.LIGHT_PURPLE;
            case 'e' -> NamedTextColor.YELLOW;
            case 'f' -> NamedTextColor.WHITE;
            default -> NamedTextColor.WHITE;
        };
    }

    /**
     * Check whether the player is muted.
     *
     * @param uuid The player's Minecraft UUID.
     * @return Yes - A {@link Player} object with corresponding information.<br/>
     * No - A {@link Player} object with all params set to nul
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
     * @return Yes - A {@link Player} object with corresponding information.<br/>
     * No - A {@link Player} object with all params set to nul
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
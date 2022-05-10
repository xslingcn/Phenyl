package live.turna.phenyl.common.instance;

import net.kyori.adventure.text.Component;

import java.util.UUID;

/**
 * <b>PSender</b><br>
 * Wrapper for CommandSender. Used across platforms.
 */
public interface PSender {
    String getUsername();

    UUID getUUID();

    void sendMessage(Component message);

    Boolean hasPermission(String node);

    String getServerName();

    Boolean isConsole();
}


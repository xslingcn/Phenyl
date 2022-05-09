package live.turna.phenyl.common.instance;

import net.kyori.adventure.text.Component;

import java.util.UUID;

public interface PSender {
    String getUsername();

    UUID getUUID();

    void sendMessage(Component message);

    Boolean hasPermission(String node);

    String getServerName();

    Boolean isConsole();
}


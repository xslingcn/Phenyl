package live.turna.phenyl.common.message.messenger;

import live.turna.phenyl.common.instance.PSender;
import net.kyori.adventure.text.Component;

public interface ServerMessenger {
    void sendPlayer(String message, PSender player);

    void sendPlayer(Component message, PSender player);

    void sendAllServer(String message);

    void sendAllServer(String message, Boolean force);

    void sendAllServer(String message, String[] exclude);

    void sendAllServer(Component message);
}

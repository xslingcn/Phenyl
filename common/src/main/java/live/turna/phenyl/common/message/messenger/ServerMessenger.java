package live.turna.phenyl.common.message.messenger;

import live.turna.phenyl.common.instance.PSender;
import net.kyori.adventure.text.Component;

public interface ServerMessenger {

    /**
     * Send message to a certain in-game player.
     *
     * @param message The message.
     * @param player  The target player.
     */
    void sendPlayer(String message, PSender player);

    /**
     * Send message to a certain in-game player.
     *
     * @param message The message.
     * @param player  The target player.
     */
    void sendPlayer(Component message, PSender player);

    /**
     * Send message to all enabled servers.
     *
     * @param message The message.
     */
    void sendAllServer(String message);

    /**
     * Send message to all enabled servers.
     *
     * @param message The message.
     */
    void sendAllServer(Component message);

    /**
     * Send message to all enabled servers, and the message would be force sent to no-messaged players.
     *
     * @param message The message.
     * @param force   Whether to send the message to all players, including the no-messaged ones.
     */
    void sendAllServer(String message, Boolean force);

    /**
     * Send message to all enabled servers, except the excluded ones.
     *
     * @param message The message.
     * @param exclude The
     */
    void sendAllServer(String message, String[] exclude);
}

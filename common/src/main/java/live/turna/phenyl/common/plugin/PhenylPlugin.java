package live.turna.phenyl.common.plugin;

import live.turna.phenyl.common.bind.BindHandler;
import live.turna.phenyl.common.database.PhenylStorage;
import live.turna.phenyl.common.database.Player;
import live.turna.phenyl.common.instance.PSender;
import live.turna.phenyl.common.instance.SenderFactory;
import live.turna.phenyl.common.listener.AbstractMiraiListenerManager;
import live.turna.phenyl.common.logger.NativeLogger;
import live.turna.phenyl.common.message.AbstractForwarder;
import live.turna.phenyl.common.message.messenger.AbstractMessenger;
import live.turna.phenyl.common.mirai.MiraiHandler;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * <b>PhenylPlugin</b><br>
 * The base interface providing abstractions throughout platforms.
 */
public interface PhenylPlugin {
    /**
     * Get Mirai handler. Can be used to get Mirai bot instance.
     *
     * @return The Mirai handler.
     */
    MiraiHandler getMirai();

    /**
     * Get message forwarder.
     *
     * @return The forwarder.
     */
    AbstractForwarder<? extends AbstractPhenyl> getForwarder();

    /**
     * Get messenger, which provides utils for sending messages.
     *
     * @return The messenger.
     */
    AbstractMessenger<? extends AbstractPhenyl> getMessenger();

    /**
     * Get the current storage instance.
     *
     * @return The storage instance.
     */
    PhenylStorage getStorage();

    /**
     * Get the plugin data directory.
     *
     * @return The directory as {@link File}.
     */
    File getDir();

    /**
     * Get the Phenyl main logger.
     *
     * @return The logger.
     */
    Logger getLogger();

    /**
     * Get the native logger, used before Log4j is loaded.
     *
     * @return The native logger.
     */
    NativeLogger getNativeLogger();

    /**
     * Get the list of muted players.
     *
     * @return The muted players.
     */
    List<Player> getMutedPlayer();

    /**
     * Get the list of no-messaged players.
     *
     * @return The no-messaged players.
     */
    List<Player> getNoMessagePlayer();

    /**
     * Get the list of all bound players;
     *
     * @return A list of player instances, of which would never hold a NULL qqid. Could be empty if no one had bound yet.
     */
    List<Player> getAllBoundPlayer();

    /**
     * Get the bound player list from storage again. Called after each binding operation.
     */
    void updateBoundPlayerList();

    /**
     * Get a player instance by username.
     *
     * @param username The player's Minecraft username.
     * @return The player instance.
     */
    PSender getPlayer(String username);

    /**
     * Get a player instance by UUID.
     *
     * @param uuid The player's Minecraft UUID.
     * @return The player instance.
     */
    PSender getPlayer(UUID uuid);

    /**
     * Get the mirai listener manager.
     *
     * @return The listener manager.
     */
    AbstractMiraiListenerManager getMiraiListenerManager();

    /**
     * Get the bind handler.
     *
     * @return The ind handler.
     */
    BindHandler getBindHandler();

    /**
     * Get a current online list of players.<br/>
     * HashMap: key - the player's server, value - the player's name.
     *
     * @return The online list.
     */
    HashMap<String, String> getOnlineList();

    /**
     * Get the status of each server.<br/>
     * Hashmap: key - the server name, value - the status, true for online, false for offline.
     *
     * @return The status.
     */
    CompletableFuture<HashMap<String, Boolean>> getStatus();

    /**
     * Get the count of total online players.
     *
     * @return The online count.
     */
    Integer getOnlineCount();

    /**
     * Get whether the platform is proxy.
     *
     * @return True - is proxy, false - not.
     */
    Boolean isProxy();

    /**
     * Get the resources as stream.
     *
     * @param name The name of target resource.
     * @return The resource as stream.
     */
    InputStream getResourceAsStream(String name);

    /**
     * Get the sender factory.
     *
     * @return The sender factory.
     */
    SenderFactory<?, ?> getSenderFactory();

    /**
     * Get the version of Phenyl.
     *
     * @return The version.
     */
    String getVersion();

    /**
     * Get a collection of all online players.
     *
     * @return The players collection.
     */
    Collection<PSender> getPlayers();

    /**
     * Get the current platform, in all upper case.
     *
     * @return The platform.
     */
    String getPlatform();

    /**
     * Register a listener.
     *
     * @param listener The listener to be registered.
     * @param <T>      The listener itself class.
     */
    <T> void registerListener(T listener);

    /**
     * Unregister all listeners.
     */
    void unregisterListeners();

}

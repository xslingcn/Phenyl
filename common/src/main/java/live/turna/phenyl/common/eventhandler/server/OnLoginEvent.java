package live.turna.phenyl.common.eventhandler.server;

import live.turna.phenyl.common.config.Config;
import live.turna.phenyl.common.instance.PSender;
import live.turna.phenyl.common.message.ImageMessage;
import live.turna.phenyl.common.plugin.AbstractPhenyl;
import live.turna.phenyl.common.utils.AvatarHelper;
import live.turna.phenyl.common.utils.MessageUtils;
import org.apache.logging.log4j.Logger;

import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static live.turna.phenyl.common.message.I18n.i18n;

/**
 * <b>OnLoginEvent</b><br>
 * Called when a server is ready to take control of a player instance.
 * Send a message to QQ group if {@code on_join} is not set to "disabled".<br>
 * Send a message to all online players if {@code on_join_broadcast} is not set to "disabled".
 *
 * @since 2021/12/4 21:52
 */
public abstract class OnLoginEvent<P extends AbstractPhenyl> {
    protected final transient P phenyl;
    protected final transient Logger LOGGER;
    protected final transient MessageUtils messageUtils;

    private transient PSender player;
    private transient String serverName;

    public OnLoginEvent(P plugin) {
        phenyl = plugin;
        messageUtils = new MessageUtils(phenyl);
        LOGGER = phenyl.getLogger();
    }

    public void fill(PSender sender, String server) {
        player = sender;
        serverName = server;
    }

    public void handle() {
        if (!Config.on_join_broadcast.equals("disabled")) {
            String joinBroadcastFormat = Config.on_join_broadcast
                    .replace("%sub_server%", messageUtils.getServerName(serverName))
                    .replace("%username%", player.getUsername());
            CompletableFuture.supplyAsync(() -> {
                phenyl.getMessenger().sendAllServer(joinBroadcastFormat, true);
                return true;
            }).orTimeout(3, TimeUnit.SECONDS);
        }

        if (!Config.on_join.equals("disabled")) {
            if (Config.on_join.startsWith("image:")) {
                CompletableFuture.supplyAsync(() ->
                        new AvatarHelper(phenyl).downloadAvatar(player.getUUID().toString())
                ).orTimeout(3, TimeUnit.SECONDS).thenApplyAsync((succeeded) -> {
                    if (!succeeded) return false;
                    try {
                        String joinFormat = Config.on_join
                                .replace("image:", "")
                                .replace("%sub_server%", messageUtils.getServerName(serverName))
                                .replace("%username%", "");
                        phenyl.getMessenger().sendImageToAll(new ImageMessage(phenyl).drawImageMessage(joinFormat, player.getUsername(), player.getUUID().toString()));
                        return true;
                    } catch (NoSuchElementException ex) {
                        LOGGER.error(i18n("noSuchGroup", ex.getLocalizedMessage()));
                        if (Config.debug) ex.printStackTrace();
                        return false;
                    }
                }).orTimeout(3, TimeUnit.SECONDS);
            } else {
                String joinFormat = Config.on_join
                        .replace("%sub_server%", messageUtils.getServerName(player.getServerName()))
                        .replace("%username%", player.getUsername());
                CompletableFuture.supplyAsync(() -> {
                    try {
                        phenyl.getMessenger().sendAllGroup(joinFormat);
                        return true;
                    } catch (NoSuchElementException ex) {
                        LOGGER.error(i18n("noSuchGroup", ex.getLocalizedMessage()));
                        if (Config.debug) ex.printStackTrace();
                        return false;
                    }
                }).orTimeout(3, TimeUnit.SECONDS);
            }
        }

        // register a player if logging in for the first time and update the player's username.
        CompletableFuture.supplyAsync(() -> {
            String uuid = player.getUUID().toString();
            String userName = player.getUsername();
            if (!phenyl.getStorage().getRegistered(uuid)) {
                phenyl.getStorage().registerPlayer(uuid, userName);
                if (Config.new_player_greeting)
                    phenyl.getMessenger().sendPlayer(i18n("newPlayer", userName), player);
            }
            return phenyl.getStorage().updateUserName(uuid, player.getUsername());
        }).orTimeout(3, TimeUnit.SECONDS);
    }
}
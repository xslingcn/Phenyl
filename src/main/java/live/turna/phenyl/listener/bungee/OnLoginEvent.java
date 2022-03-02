package live.turna.phenyl.listener.bungee;

import live.turna.phenyl.Phenyl;
import live.turna.phenyl.config.PhenylConfiguration;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static live.turna.phenyl.Phenyl.LOGGER;
import static live.turna.phenyl.message.I18n.i18n;
import static live.turna.phenyl.message.ImageMessage.drawImageMessage;
import static live.turna.phenyl.utils.Avatar.downloadAvatar;
import static live.turna.phenyl.utils.Message.broadcastMessage;
import static live.turna.phenyl.utils.Message.getServerName;
import static live.turna.phenyl.utils.Message.sendMessage;
import static live.turna.phenyl.utils.Mirai.sendGroup;
import static live.turna.phenyl.utils.Mirai.sendImage;

/**
 * <b>OnLoginEvent</b><br>
 * Called when a server is ready to take control of a player instance.
 * Send a message to QQ group if {@code on_join} is not set to "disabled".<br>
 * Send a message to all online players if {@code on_join_broadcast} is not set to "disabled".
 *
 * @since 2021/12/4 21:52
 */
public class OnLoginEvent implements Listener {
    private final transient Phenyl phenyl = Phenyl.getInstance();

    @EventHandler
    public void onLogin(ServerConnectedEvent e) {
        if (!PhenylConfiguration.on_join_broadcast.equals("disabled")) {
            String joinBroadcastFormat = PhenylConfiguration.on_join_broadcast
                    .replace("%sub_server%", getServerName(e.getServer()))
                    .replace("%username%", e.getPlayer().getName());
            CompletableFuture.supplyAsync(() -> {
                broadcastMessage(joinBroadcastFormat);
                return true;
            }).orTimeout(3, TimeUnit.SECONDS);
        }

        if (!PhenylConfiguration.on_join.equals("disabled")) {
            if (PhenylConfiguration.on_join.startsWith("image:")) {
                CompletableFuture.supplyAsync(() ->
                        downloadAvatar(e.getPlayer().getUniqueId().toString())
                ).orTimeout(3, TimeUnit.SECONDS).thenApplyAsync((succeeded) -> {
                    if (!succeeded) return false;
                    try {
                        String joinFormat = PhenylConfiguration.on_join
                                .replace("image:", "")
                                .replace("%sub_server%", getServerName(e.getServer()))
                                .replace("%username%", "");
                        sendImage(drawImageMessage(joinFormat, e.getPlayer().getName(), e.getPlayer().getUniqueId().toString()));
                        return true;
                    } catch (NoSuchElementException ex) {
                        LOGGER.error(i18n("noSuchGroup", ex.getLocalizedMessage()));
                        if (PhenylConfiguration.debug) ex.printStackTrace();
                        return false;
                    }
                }).orTimeout(3, TimeUnit.SECONDS);
            } else {
                String joinFormat = PhenylConfiguration.on_join
                        .replace("%sub_server%", getServerName(e.getServer()))
                        .replace("%username%", e.getPlayer().getName());
                CompletableFuture.supplyAsync(() -> {
                    try {
                        sendGroup(joinFormat);
                        return true;
                    } catch (NoSuchElementException ex) {
                        LOGGER.error(i18n("noSuchGroup", ex.getLocalizedMessage()));
                        if (PhenylConfiguration.debug) ex.printStackTrace();
                        return false;
                    }
                }).orTimeout(3, TimeUnit.SECONDS);
            }
        }

        // register a player if logging in for the first time and update the player's username.
        CompletableFuture.supplyAsync(() -> {
            String uuid = e.getPlayer().getUniqueId().toString();
            String userName = e.getPlayer().getName();
            if (!phenyl.getDatabase().getRegistered(uuid)) {
                phenyl.getDatabase().registerPlayer(uuid, userName);
                if (PhenylConfiguration.new_player_greeting)
                    sendMessage(i18n("newPlayer", userName), e.getPlayer());
            }
            return phenyl.getDatabase().updateUserName(uuid, e.getPlayer().getName());
        }).orTimeout(3, TimeUnit.SECONDS);
    }
}
package live.turna.phenyl.listener.bungee;

import live.turna.phenyl.Phenyl;
import live.turna.phenyl.PhenylListener;
import live.turna.phenyl.config.PhenylConfiguration;
import live.turna.phenyl.database.Database;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.event.EventHandler;

import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static live.turna.phenyl.message.I18n.i18n;
import static live.turna.phenyl.utils.Message.broadcastMessage;
import static live.turna.phenyl.utils.Message.getServerName;
import static live.turna.phenyl.utils.Mirai.sendGroup;

/**
 * <b>OnLoginEvent</b><br>
 * Called when a server is ready to take control of a player instance.
 * Send a message to QQ group if {@code on_join} is not set to "disabled".<br>
 * Send a message to all online players if {@code on_join_broadcast} is not set to "disabled".
 *
 * @since 2021/12/4 21:52
 */
public class OnLoginEvent extends PhenylListener {
    @EventHandler
    public void OnLogin(ServerConnectedEvent e) {
        if (!PhenylConfiguration.on_join.equals("disabled")) {
            String joinFormat = PhenylConfiguration.on_join
                    .replace("%sub_server%", getServerName(e.getServer()))
                    .replace("%username%", e.getPlayer().getName());
            CompletableFuture<Boolean> futureQQ = CompletableFuture.supplyAsync(() -> {
                for (Long id : PhenylConfiguration.enabled_groups) {
                    try {
                        sendGroup(Phenyl.getMiraiInstance().getBot().getGroupOrFail(id), joinFormat);
                    } catch (NoSuchElementException ex) {
                        LOGGER.error(i18n("noSuchGroup"));
                        if (PhenylConfiguration.debug) ex.printStackTrace();
                        return false;
                    }
                }
                return true;
            }).orTimeout(3, TimeUnit.SECONDS);
        }
        if (!PhenylConfiguration.on_join_broadcast.equals("disabled")) {
            String joinBroadcastFormat = PhenylConfiguration.on_join_broadcast
                    .replace("%sub_server%", getServerName(e.getServer()))
                    .replace("%username%", e.getPlayer().getName());
            CompletableFuture<Boolean> futureBroadcast = CompletableFuture.supplyAsync(() -> {
                broadcastMessage(joinBroadcastFormat);
                return true;
            }).orTimeout(3, TimeUnit.SECONDS);
        }

        // Register a player if logging in for the first time. Updating the player's username if username not found.
        String uuid = e.getPlayer().getUniqueId().toString();
        String userName = e.getPlayer().getName();
        Integer id = Database.getIDByUserName(userName);
        if (id == null)
            id = Database.registerPlayer(uuid).id();
        Database.updateUserName(id.toString(), e.getPlayer().getName());

    }
}
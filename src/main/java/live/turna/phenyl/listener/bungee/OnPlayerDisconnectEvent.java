package live.turna.phenyl.listener.bungee;

import live.turna.phenyl.PhenylListener;
import live.turna.phenyl.config.PhenylConfiguration;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.event.EventHandler;

import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static live.turna.phenyl.message.I18n.i18n;
import static live.turna.phenyl.message.ImageMessage.drawImageMessage;
import static live.turna.phenyl.utils.Message.broadcastMessage;
import static live.turna.phenyl.utils.Mirai.sendGroup;
import static live.turna.phenyl.utils.Mirai.sendImage;

/**
 * <b>OnPlayerDisconnectEvent</b><br>
 * Called when a player has disconnected from bungee.
 * Send a message to QQ group if {@code on_leave} is not set to "disabled".<br>
 * Send a message to all online players if {@code on_leave_broadcast} is not set to "disabled".
 *
 * @since 2021/12/4 21:51
 */
public class OnPlayerDisconnectEvent extends PhenylListener {
    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent e) {
        if (!PhenylConfiguration.on_leave_broadcast.equals("disabled")) {
            String leaveBroadcastFormat = PhenylConfiguration.on_leave_broadcast
                    .replace("%username%", e.getPlayer().getName());
            CompletableFuture<Boolean> futureBroadcast = CompletableFuture.supplyAsync(() -> {
                broadcastMessage(leaveBroadcastFormat);
                return true;
            }).orTimeout(3, TimeUnit.SECONDS);
        }

        if (!PhenylConfiguration.on_leave.equals("disabled")) {
            if (PhenylConfiguration.on_leave.startsWith("image:")) {
                CompletableFuture<Boolean> futureImage = CompletableFuture.supplyAsync(() -> {
                    try {
                        String joinFormat = PhenylConfiguration.on_leave
                                .replace("image:", "")
                                .replace("%username%", "");
                        sendImage(drawImageMessage(joinFormat, e.getPlayer().getName(), e.getPlayer().getUniqueId().toString()));
                    } catch (NoSuchElementException ex) {
                        LOGGER.error(i18n("noSuchGroup"));
                        if (PhenylConfiguration.debug) ex.printStackTrace();
                        return false;
                    }
                    return true;
                }).orTimeout(3, TimeUnit.SECONDS);
            } else {
                String leaveFormat = PhenylConfiguration.on_leave
                        .replace("%username%", e.getPlayer().getName());
                CompletableFuture<Boolean> futurePlain = CompletableFuture.supplyAsync(() -> {
                    try {
                        sendGroup(leaveFormat);
                    } catch (NoSuchElementException ex) {
                        LOGGER.error(i18n("noSuchGroup"));
                        if (PhenylConfiguration.debug) ex.printStackTrace();
                        return false;
                    }
                    return true;
                }).orTimeout(3, TimeUnit.SECONDS);
            }
        }
    }
}
package live.turna.phenyl.common.eventhandler.server;

import live.turna.phenyl.common.config.Config;
import live.turna.phenyl.common.instance.PSender;
import live.turna.phenyl.common.message.ImageMessage;
import live.turna.phenyl.common.plugin.AbstractPhenyl;
import live.turna.phenyl.common.utils.MessageUtils;
import org.apache.logging.log4j.Logger;

import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static live.turna.phenyl.common.message.I18n.i18n;

/**
 * <b>OnPlayerDisconnectEvent</b><br>
 * Called when a player has disconnected from bungee.
 * Send a message to QQ group if {@code on_leave} is not set to "disabled".<br>
 * Send a message to all online players if {@code on_leave_broadcast} is not set to "disabled".
 *
 * @since 2021/12/4 21:51
 */
public abstract class OnPlayerDisconnectEvent<P extends AbstractPhenyl> {
    protected final transient P phenyl;
    protected final transient MessageUtils messageUtils;
    protected final transient Logger LOGGER;

    private transient PSender player;

    public OnPlayerDisconnectEvent(P plugin) {
        phenyl = plugin;
        messageUtils = new MessageUtils(phenyl);
        LOGGER = phenyl.getLogger();
    }

    public void fill(PSender player) {
        this.player = player;
    }

    public void handle() {
        if (!Config.on_leave_broadcast.equals("disabled")) {
            String leaveBroadcastFormat = Config.on_leave_broadcast
                    .replace("%username%", player.getUsername());
            CompletableFuture.supplyAsync(() -> {
                phenyl.getMessenger().sendAllServer(leaveBroadcastFormat, true);
                return true;
            }).orTimeout(3, TimeUnit.SECONDS);
        }

        if (!Config.on_leave.equals("disabled")) {
            if (Config.on_leave.startsWith("image:")) {
                CompletableFuture.supplyAsync(() -> {
                    try {
                        String joinFormat = Config.on_leave
                                .replace("image:", "")
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
                String leaveFormat = Config.on_leave
                        .replace("%username%", player.getUsername());
                CompletableFuture.supplyAsync(() -> {
                    try {
                        phenyl.getMessenger().sendAllGroup(leaveFormat);
                        return true;
                    } catch (NoSuchElementException ex) {
                        LOGGER.error(i18n("noSuchGroup", ex.getLocalizedMessage()));
                        if (Config.debug) ex.printStackTrace();
                        return false;
                    }
                }).orTimeout(3, TimeUnit.SECONDS);
            }
        }
    }
}
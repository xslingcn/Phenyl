package live.turna.phenyl.listener.bungee;

import live.turna.phenyl.Phenyl;
import live.turna.phenyl.PhenylListener;
import live.turna.phenyl.config.PhenylConfiguration;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.event.EventHandler;

import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static live.turna.phenyl.message.I18n.i18n;
import static live.turna.phenyl.utils.Message.broadcastMessage;
import static live.turna.phenyl.utils.Mirai.sendGroup;

/**
 * <b>OnPlayerDisconnectEvent</b><br>
 * * @since 2021/12/4 21:51
 */
public class OnPlayerDisconnectEvent extends PhenylListener {
    @EventHandler
    public void OnPlayerDisconnect(PlayerDisconnectEvent e) {
        if (!PhenylConfiguration.on_leave.equals("disabled")) {
            String leaveFormat = PhenylConfiguration.on_leave
                    .replace("%username%", e.getPlayer().getName());
            CompletableFuture<Boolean> futureQQ = CompletableFuture.supplyAsync(() -> {
                for (Long id : PhenylConfiguration.enabled_groups) {
                    try {
                        sendGroup(Phenyl.getMiraiInstance().getBot().getGroupOrFail(id), leaveFormat);
                    } catch (NoSuchElementException ex) {
                        LOGGER.error(i18n("noSuchGroup"));
                        if (PhenylConfiguration.debug) ex.printStackTrace();
                        return false;
                    }
                }
                return true;
            }).orTimeout(3, TimeUnit.SECONDS);
        }
        if (!PhenylConfiguration.on_leave_broadcast.equals("disabled")) {
            String leaveBroadcastFormat = PhenylConfiguration.on_leave_broadcast
                    .replace("%username%", e.getPlayer().getName());
            CompletableFuture<Boolean> futureBroadcast = CompletableFuture.supplyAsync(() -> {
                broadcastMessage(leaveBroadcastFormat);
                return true;
            }).orTimeout(3, TimeUnit.SECONDS);
        }

    }
}
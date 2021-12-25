package live.turna.phenyl.listener.bungee;

import live.turna.phenyl.PhenylListener;
import live.turna.phenyl.config.PhenylConfiguration;

import static live.turna.phenyl.message.Forward.forwardToQQ;
import static live.turna.phenyl.message.I18n.i18n;
import static live.turna.phenyl.utils.Message.broadcastMessage;
import static live.turna.phenyl.utils.Message.getServerName;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.event.EventHandler;

import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * <b>OnBungeeChatEvent</b><br>
 *
 * @since 2021/12/4 21:54
 */
public class OnChatEvent extends PhenylListener {

    @EventHandler
    public void onChatEvent(ChatEvent e) {
        if (e.isCommand() || e.isProxyCommand())
            return;
        ProxiedPlayer player = (ProxiedPlayer) e.getSender();
        if (!PhenylConfiguration.enabled_servers.contains(player.getServer().getInfo().getName())) return;
        switch (PhenylConfiguration.forward_mode) {
            case "sync", "bind" -> {
                CompletableFuture<Boolean> futureSync = CompletableFuture.supplyAsync(() -> {
                    try {
                        forwardToQQ(PhenylConfiguration.enabled_groups, e.getMessage(), player.getName(), player.getUniqueId().toString(), getServerName(player.getServer()));
                    } catch (NoSuchElementException ex) {
                        LOGGER.error(i18n("noSuchGroup"));
                        if (PhenylConfiguration.debug) ex.printStackTrace();
                        return false;
                    }
                    return true;
                }).orTimeout(3, TimeUnit.SECONDS);
            }
        }

        if (!PhenylConfiguration.cross_sever_format.equals("disabled")) {
            String format = PhenylConfiguration.cross_sever_format
                    .replace("%sub_server%", getServerName((player.getServer())))
                    .replace("%username%", player.getName())
                    .replace("%message%", e.getMessage());
            CompletableFuture<Boolean> futureBind = CompletableFuture.supplyAsync(() -> {
                broadcastMessage(format, new Server[]{player.getServer()});
                return true;
            }).orTimeout(3, TimeUnit.SECONDS);
        }
    }
}
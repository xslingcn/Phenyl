package live.turna.phenyl.bungee.listener.bungee;

import live.turna.phenyl.bungee.BungeePhenyl;
import live.turna.phenyl.common.config.Config;
import live.turna.phenyl.common.eventhandler.server.OnPlayerDisconnectEvent;
import live.turna.phenyl.common.instance.AbstractSender;
import live.turna.phenyl.common.instance.PSender;
import live.turna.phenyl.common.message.ImageMessage;
import live.turna.phenyl.common.plugin.AbstractPhenyl;
import live.turna.phenyl.common.utils.MessageUtils;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.apache.logging.log4j.Logger;

import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static live.turna.phenyl.common.message.I18n.i18n;

/**
 * <b>BungeeOnPlayerDisconnectEvent</b><br>
 * Called when a player has disconnected from bungee.
 * Send a message to QQ group if {@code on_leave} is not set to "disabled".<br>
 * Send a message to all online players if {@code on_leave_broadcast} is not set to "disabled".
 *
 * @since 2021/12/4 21:51
 */
public class BungeeOnPlayerDisconnectEvent extends OnPlayerDisconnectEvent<BungeePhenyl> implements Listener {

    public BungeeOnPlayerDisconnectEvent(BungeePhenyl plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent e){
        super.fill(phenyl.getSenderFactory().wrap(e.getPlayer()));
        super.handle();
    }
}
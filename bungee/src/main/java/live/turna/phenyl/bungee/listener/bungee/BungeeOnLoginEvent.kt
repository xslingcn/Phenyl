package live.turna.phenyl.bungee.listener.bungee;

import live.turna.phenyl.bungee.BungeePhenyl;
import live.turna.phenyl.common.eventhandler.server.OnLoginEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * <b>BungeeOnLoginEvent</b><br>
 * Called when a server is ready to take control of a player instance.
 * Send a message to QQ group if {@code on_join} is not set to "disabled".<br>
 * Send a message to all online players if {@code on_join_broadcast} is not set to "disabled".
 *
 * @since 2021/12/4 21:52
 */
public class BungeeOnLoginEvent extends OnLoginEvent<BungeePhenyl> implements Listener {

    public BungeeOnLoginEvent(BungeePhenyl plugin) {
        super(plugin);
    }

    @EventHandler
    public void onLoginEvent(ServerConnectedEvent e) {
        super.fill(phenyl.getSenderFactory().wrap(e.getPlayer()), messageUtils.getServerName(e.getServer().getInfo().getName()));
        super.handle();
    }
}
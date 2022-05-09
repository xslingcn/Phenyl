package live.turna.phenyl.bungee.listener.bungee

import live.turna.phenyl.bungee.BungeePhenyl
import live.turna.phenyl.common.eventhandler.server.OnPlayerDisconnectEvent
import net.md_5.bungee.api.event.PlayerDisconnectEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler

/**
 * **BungeeOnPlayerDisconnectEvent**<br></br>
 * Called when a player has disconnected from bungee.
 * Send a message to QQ group if `on_leave` is not set to "disabled".<br></br>
 * Send a message to all online players if `on_leave_broadcast` is not set to "disabled".
 *
 * @since 2021/12/4 21:51
 */
class BungeeOnPlayerDisconnectEvent(plugin: BungeePhenyl) : OnPlayerDisconnectEvent<BungeePhenyl>(plugin), Listener {
    @EventHandler
    fun onPlayerDisconnect(e: PlayerDisconnectEvent) {
        super.fill(phenyl.senderFactory.wrap(e.player))
        super.handle()
    }
}
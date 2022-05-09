package live.turna.phenyl.bungee.listener.bungee

import live.turna.phenyl.bungee.BungeePhenyl
import live.turna.phenyl.common.eventhandler.server.OnLoginEvent
import net.md_5.bungee.api.event.ServerConnectedEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler

/**
 * **BungeeOnLoginEvent**<br></br>
 * Called when a server is ready to take control of a player instance.
 * Send a message to QQ group if `on_join` is not set to "disabled".<br></br>
 * Send a message to all online players if `on_join_broadcast` is not set to "disabled".
 *
 * @since 2021/12/4 21:52
 */
class BungeeOnLoginEvent(plugin: BungeePhenyl) : OnLoginEvent<BungeePhenyl>(plugin), Listener {
    @EventHandler
    fun onLoginEvent(e: ServerConnectedEvent) {
        super.fill(phenyl.senderFactory.wrap(e.player), messageUtils.getServerName(e.server.info.name))
        super.handle()
    }
}
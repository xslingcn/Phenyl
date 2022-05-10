package live.turna.phenyl.bungee.listener.bungee

import live.turna.phenyl.bungee.BungeePhenyl
import live.turna.phenyl.common.eventhandler.server.OnLoginEvent
import net.md_5.bungee.api.event.ServerConnectedEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler

class BungeeOnLoginEvent(plugin: BungeePhenyl) : OnLoginEvent<BungeePhenyl>(plugin), Listener {
    @EventHandler
    fun onLoginEvent(e: ServerConnectedEvent) {
        super.fill(phenyl.senderFactory.wrap(e.player), messageUtils.getServerName(e.server.info.name))
        super.handle()
    }
}
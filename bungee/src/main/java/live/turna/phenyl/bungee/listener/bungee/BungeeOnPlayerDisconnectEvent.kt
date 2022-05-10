package live.turna.phenyl.bungee.listener.bungee

import live.turna.phenyl.bungee.BungeePhenyl
import live.turna.phenyl.common.eventhandler.server.OnPlayerDisconnectEvent
import net.md_5.bungee.api.event.PlayerDisconnectEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler

class BungeeOnPlayerDisconnectEvent(plugin: BungeePhenyl) : OnPlayerDisconnectEvent<BungeePhenyl>(plugin), Listener {
    @EventHandler
    fun onPlayerDisconnect(e: PlayerDisconnectEvent) {
        super.fill(phenyl.senderFactory.wrap(e.player))
        super.handle()
    }
}
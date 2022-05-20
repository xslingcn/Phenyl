package live.turna.phenyl.velocity.listener.velocity

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import live.turna.phenyl.common.eventhandler.server.OnPlayerDisconnectEvent
import live.turna.phenyl.velocity.VelocityPhenyl

class VelocityOnPlayerDisconnectEvent(plugin: VelocityPhenyl) : OnPlayerDisconnectEvent<VelocityPhenyl>(plugin) {
    @Subscribe
    fun onPlayerDisconnect(e: DisconnectEvent) {
        super.fill(phenyl.senderFactory.wrap(e.player))
        super.handle()
    }
}
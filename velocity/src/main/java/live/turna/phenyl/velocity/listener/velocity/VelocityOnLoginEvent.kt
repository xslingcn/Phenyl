package live.turna.phenyl.velocity.listener.velocity

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.player.ServerConnectedEvent
import live.turna.phenyl.common.eventhandler.server.OnLoginEvent
import live.turna.phenyl.velocity.VelocityPhenyl

class VelocityOnLoginEvent(plugin: VelocityPhenyl) : OnLoginEvent<VelocityPhenyl>(plugin) {
    @Subscribe
    fun onLogin(e: ServerConnectedEvent) {
        super.fill(phenyl.senderFactory.wrap(e.player), e.server.serverInfo.name)
        super.handle()
    }
}
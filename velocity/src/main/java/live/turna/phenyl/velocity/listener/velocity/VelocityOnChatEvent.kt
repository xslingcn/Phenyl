package live.turna.phenyl.velocity.listener.velocity

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.player.PlayerChatEvent
import live.turna.phenyl.common.eventhandler.server.OnChatEvent
import live.turna.phenyl.velocity.VelocityPhenyl

/**
 * **VelocityOnPlayerChatEvent**
 *
 * @since 2022/5/21 3:13
 */
class VelocityOnChatEvent(plugin: VelocityPhenyl) : OnChatEvent<VelocityPhenyl>(plugin) {
    @Subscribe
    fun onChat(e: PlayerChatEvent) {
        super.fill(phenyl.senderFactory.wrap(e.player), e.message)
        super.handle()
    }
}
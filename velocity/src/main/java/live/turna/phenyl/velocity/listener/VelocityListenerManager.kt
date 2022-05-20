package live.turna.phenyl.velocity.listener

import live.turna.phenyl.common.listener.AbstractServerListenerManager
import live.turna.phenyl.velocity.VelocityPhenyl
import live.turna.phenyl.velocity.listener.mirai.VelocityOnBotOfflineEvent
import live.turna.phenyl.velocity.listener.mirai.VelocityOnGroupMessageEvent
import live.turna.phenyl.velocity.listener.velocity.VelocityOnChatEvent
import live.turna.phenyl.velocity.listener.velocity.VelocityOnLoginEvent
import live.turna.phenyl.velocity.listener.velocity.VelocityOnPlayerDisconnectEvent

class VelocityListenerManager(plugin: VelocityPhenyl) : AbstractServerListenerManager<VelocityPhenyl>(plugin) {
    override fun start() {
        // Mirai
        phenyl.registerListener(VelocityOnBotOfflineEvent(phenyl))
        phenyl.registerListener(VelocityOnGroupMessageEvent(phenyl))

        // Velocity
        phenyl.registerListener(VelocityOnChatEvent(phenyl))
        phenyl.registerListener(VelocityOnLoginEvent(phenyl))
        phenyl.registerListener(VelocityOnPlayerDisconnectEvent(phenyl))
    }

}
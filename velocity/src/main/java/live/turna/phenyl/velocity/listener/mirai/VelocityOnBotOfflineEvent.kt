package live.turna.phenyl.velocity.listener.mirai

import com.velocitypowered.api.event.Subscribe
import live.turna.phenyl.common.eventhandler.mirai.OnBotOfflineEvent
import live.turna.phenyl.velocity.VelocityPhenyl
import live.turna.phenyl.velocity.event.VelocityBotOfflineEvent

class VelocityOnBotOfflineEvent(plugin: VelocityPhenyl) : OnBotOfflineEvent<VelocityPhenyl>(plugin) {
    @Subscribe
    fun onBotOffline(e: VelocityBotOfflineEvent) {
        super.fill(e.type, e.id)
        super.handle()
    }
}
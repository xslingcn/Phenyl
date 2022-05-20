package live.turna.phenyl.velocity.listener.mirai

import com.velocitypowered.api.event.Subscribe
import live.turna.phenyl.common.eventhandler.mirai.OnGroupMessageEvent
import live.turna.phenyl.velocity.VelocityPhenyl
import live.turna.phenyl.velocity.event.VelocityGroupMessageEvent

class VelocityOnGroupMessageEvent(plugin: VelocityPhenyl) : OnGroupMessageEvent<VelocityPhenyl>(plugin) {
    @Subscribe
    fun onGroupMessage(e: VelocityGroupMessageEvent) {
        super.fill(e.group, e.senderId, e.message, e.senderNameCardOrNick)
        super.handle()
    }
}
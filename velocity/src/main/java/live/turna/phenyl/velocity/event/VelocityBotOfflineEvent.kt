package live.turna.phenyl.velocity.event

import live.turna.phenyl.mirai.event.PBotOfflineEvent
import net.mamoe.mirai.event.events.BotOfflineEvent

class VelocityBotOfflineEvent(private val event: BotOfflineEvent) : PBotOfflineEvent {
    override val type: String
        get() = event.javaClass.toString().split("\$").toTypedArray()[1]
    override val id: Long
        get() = event.bot.id
}
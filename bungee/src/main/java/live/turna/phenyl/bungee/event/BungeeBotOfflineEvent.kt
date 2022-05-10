package live.turna.phenyl.bungee.event

import live.turna.phenyl.mirai.event.PBotOfflineEvent
import net.mamoe.mirai.event.events.BotOfflineEvent
import net.md_5.bungee.api.plugin.Event

class BungeeBotOfflineEvent(private val event: BotOfflineEvent) : Event(), PBotOfflineEvent {
    override val type: String
        get() = event.javaClass.toString().split("\$").toTypedArray()[1]
    override val id: Long
        get() = event.bot.id
}
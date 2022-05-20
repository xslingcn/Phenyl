package live.turna.phenyl.velocity.listener

import live.turna.phenyl.common.listener.AbstractMiraiListenerManager
import live.turna.phenyl.velocity.VelocityPhenyl
import live.turna.phenyl.velocity.event.VelocityBotOfflineEvent
import live.turna.phenyl.velocity.event.VelocityGroupMessageEvent
import net.mamoe.mirai.Bot
import net.mamoe.mirai.event.events.BotOfflineEvent
import net.mamoe.mirai.event.events.GroupMessageEvent

class MiraiListenerManager(val phenyl: VelocityPhenyl) : AbstractMiraiListenerManager() {
    override fun start(bot: Bot) {
        val eventChannel = bot.eventChannel
        BotOfflineListener =
            bot.eventChannel.subscribeAlways<BotOfflineEvent> { e ->
                phenyl.loader.proxy.eventManager.fire(
                    VelocityBotOfflineEvent(e)
                )
            }
        GroupMessageListener = eventChannel.subscribeAlways<GroupMessageEvent> { e ->
            phenyl.loader.proxy.eventManager.fire(
                VelocityGroupMessageEvent(e)
            )
        }
    }

}
package live.turna.phenyl.bungee.listener

import live.turna.phenyl.bungee.event.BungeeBotOfflineEvent
import live.turna.phenyl.bungee.event.BungeeGroupMessageEvent
import live.turna.phenyl.common.listener.AbstractMiraiListenerManager
import net.mamoe.mirai.Bot
import net.mamoe.mirai.event.events.BotOfflineEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.md_5.bungee.api.ProxyServer

class MiraiListenerManager : AbstractMiraiListenerManager() {
    override fun start(bot: Bot) {
        val eventChannel = bot.eventChannel
        BotOfflineListener =
            bot.eventChannel.subscribeAlways<BotOfflineEvent> { e ->
                ProxyServer.getInstance().pluginManager.callEvent(
                    BungeeBotOfflineEvent(e)
                )
            }
        GroupMessageListener = eventChannel.subscribeAlways<GroupMessageEvent> { e ->
            ProxyServer.getInstance().pluginManager.callEvent(
                BungeeGroupMessageEvent(e)
            )
        }
    }
}
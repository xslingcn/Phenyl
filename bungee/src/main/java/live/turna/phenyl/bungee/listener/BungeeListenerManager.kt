package live.turna.phenyl.bungee.listener

import live.turna.phenyl.bungee.BungeePhenyl
import live.turna.phenyl.bungee.listener.bungee.BungeeOnChatEvent
import live.turna.phenyl.bungee.listener.bungee.BungeeOnLoginEvent
import live.turna.phenyl.bungee.listener.bungee.BungeeOnPlayerDisconnectEvent
import live.turna.phenyl.bungee.listener.mirai.BungeeOnBotOfflineEvent
import live.turna.phenyl.bungee.listener.mirai.BungeeOnGroupMessageEvent
import live.turna.phenyl.common.listener.AbstractServerListenerManager

/**
 * **BungeeListenerManager**<br></br>
 * *
 *
 * @since 2022/5/6 15:43
 */
class BungeeListenerManager(plugin: BungeePhenyl) : AbstractServerListenerManager<BungeePhenyl>(plugin) {
    override fun start() {
        // Mirai
        phenyl.registerListener(BungeeOnBotOfflineEvent(phenyl))
        phenyl.registerListener(BungeeOnGroupMessageEvent(phenyl))

        // Bungee
        phenyl.registerListener(BungeeOnChatEvent(phenyl))
        phenyl.registerListener(BungeeOnLoginEvent(phenyl))
        phenyl.registerListener(BungeeOnPlayerDisconnectEvent(phenyl))
    }
}
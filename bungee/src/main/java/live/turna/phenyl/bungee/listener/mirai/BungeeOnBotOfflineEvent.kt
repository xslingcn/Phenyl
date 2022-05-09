package live.turna.phenyl.bungee.listener.mirai

import live.turna.phenyl.bungee.BungeePhenyl
import live.turna.phenyl.bungee.event.BungeeBotOfflineEvent
import live.turna.phenyl.common.eventhandler.mirai.OnBotOfflineEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler

/**
 * **BungeeOnBotOfflineEvent**<br></br>
 * Listener on BotOfflineEvent.<br></br>
 * If forced offline, log to tell operators to re-login.
 *
 * @see net.mamoe.mirai.event.events.BotOfflineEvent
 *
 * @since 2021/12/4 18:55
 */
class BungeeOnBotOfflineEvent(plugin: BungeePhenyl) : OnBotOfflineEvent<BungeePhenyl>(plugin), Listener {
    @EventHandler
    fun onBotOffline(e: BungeeBotOfflineEvent) {
        super.fill(e.type, e.id)
        super.handle()
    }
}
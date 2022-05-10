package live.turna.phenyl.bungee.listener.mirai

import live.turna.phenyl.bungee.BungeePhenyl
import live.turna.phenyl.bungee.event.BungeeBotOfflineEvent
import live.turna.phenyl.common.eventhandler.mirai.OnBotOfflineEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler

class BungeeOnBotOfflineEvent(plugin: BungeePhenyl) : OnBotOfflineEvent<BungeePhenyl>(plugin), Listener {
    @EventHandler
    fun onBotOffline(e: BungeeBotOfflineEvent) {
        super.fill(e.type, e.id)
        super.handle()
    }
}
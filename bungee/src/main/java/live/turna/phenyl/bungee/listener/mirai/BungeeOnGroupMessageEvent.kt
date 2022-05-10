package live.turna.phenyl.bungee.listener.mirai

import live.turna.phenyl.bungee.BungeePhenyl
import live.turna.phenyl.bungee.event.BungeeGroupMessageEvent
import live.turna.phenyl.common.eventhandler.mirai.OnGroupMessageEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler

class BungeeOnGroupMessageEvent(plugin: BungeePhenyl) : OnGroupMessageEvent<BungeePhenyl>(plugin), Listener {
    @EventHandler
    fun onGroupMessage(e: BungeeGroupMessageEvent) {
        super.fill(e.group, e.senderId, e.message, e.senderNameCardOrNick)
        super.handle()
    }
}
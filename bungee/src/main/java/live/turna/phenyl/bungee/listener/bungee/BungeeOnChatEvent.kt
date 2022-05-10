package live.turna.phenyl.bungee.listener.bungee

import live.turna.phenyl.bungee.BungeePhenyl
import live.turna.phenyl.common.eventhandler.server.OnChatEvent
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.ChatEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler

class BungeeOnChatEvent(plugin: BungeePhenyl) : OnChatEvent<BungeePhenyl>(plugin), Listener {
    @EventHandler
    fun onChat(e: ChatEvent) {
        if (e.isCommand || e.isProxyCommand) return
        super.fill(phenyl.senderFactory.wrap(e.sender as ProxiedPlayer), e.message)
        super.handle()
    }
}
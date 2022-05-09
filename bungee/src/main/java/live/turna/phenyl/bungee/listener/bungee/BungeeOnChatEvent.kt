package live.turna.phenyl.bungee.listener.bungee

import live.turna.phenyl.bungee.BungeePhenyl
import live.turna.phenyl.common.eventhandler.server.OnChatEvent
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.ChatEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler

/**
 * **OnBungeeChatEvent**<br></br>
 * Called when player sent a chat message.
 * Forward the message to QQ group.<br></br>
 * Forward the message to all online players in other sub-servers if `cross_sever_format` is not set to "disabled".
 *
 * @since 2021/12/4 21:54
 */
class BungeeOnChatEvent(plugin: BungeePhenyl) : OnChatEvent<BungeePhenyl>(plugin), Listener {
    @EventHandler
    fun onChat(e: ChatEvent) {
        if (e.isCommand || e.isProxyCommand) return
        super.fill(phenyl.senderFactory.wrap(e.sender as ProxiedPlayer), e.message)
        super.handle()
    }
}
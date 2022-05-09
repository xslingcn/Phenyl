package live.turna.phenyl.bungee.listener.mirai

import live.turna.phenyl.bungee.BungeePhenyl
import live.turna.phenyl.bungee.event.BungeeGroupMessageEvent
import live.turna.phenyl.common.eventhandler.mirai.OnGroupMessageEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler

/**
 * **BungeeOnGroupMessageEvent**<br></br>
 * Listener on GroupMessageEvent.<br></br>
 * This produces all group messages including commands and chats.
 *
 * @since 2021/12/4 18:55
 */
class BungeeOnGroupMessageEvent(plugin: BungeePhenyl) : OnGroupMessageEvent<BungeePhenyl>(plugin), Listener {
    @EventHandler
    fun onGroupMessage(e: BungeeGroupMessageEvent) {
        super.fill(e.group, e.senderId, e.message, e.senderNameCardOrNick)
        super.handle()
    }
}
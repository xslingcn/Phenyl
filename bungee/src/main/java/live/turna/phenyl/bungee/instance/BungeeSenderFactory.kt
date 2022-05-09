package live.turna.phenyl.bungee.instance

import live.turna.phenyl.bungee.BungeePhenyl
import live.turna.phenyl.common.instance.AbstractSender
import live.turna.phenyl.common.instance.PSender
import live.turna.phenyl.common.instance.SenderFactory
import net.kyori.adventure.platform.bungeecord.BungeeAudiences
import net.kyori.adventure.text.Component
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import java.util.*

/**
 * **BungeeSenderFactory**<br></br>
 *
 * @since 2022/5/3 13:12
 */
class BungeeSenderFactory(plugin: BungeePhenyl) : SenderFactory<BungeePhenyl, CommandSender>(plugin) {
    private val audiences: BungeeAudiences

    init {
        audiences = BungeeAudiences.create(plugin.loader)
    }

    override fun getUniqueId(sender: CommandSender): UUID {
        if (sender is ProxiedPlayer) return sender.uniqueId
        return UUID(0, 0)
    }

    override fun getName(sender: CommandSender): String {
        return sender.name
    }

    override fun sendMessage(sender: CommandSender, message: Component) {
        audiences.sender(sender).sendMessage(message)
    }

    override fun hasPermission(sender: CommandSender, node: String): Boolean {
        return sender.hasPermission(node)
    }

    override fun isConsole(sender: CommandSender): Boolean {
        return sender !is ProxiedPlayer
    }

    override fun getServerName(sender: CommandSender): String {
        if (sender is ProxiedPlayer) return sender.server.info.name
        return "CONSOLE"
    }

    override fun wrap(sender: CommandSender?): PSender? {
        if (sender == null) return null
        return AbstractSender(phenyl, this, sender)
    }


}
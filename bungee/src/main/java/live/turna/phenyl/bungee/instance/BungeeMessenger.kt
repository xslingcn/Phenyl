package live.turna.phenyl.bungee.instance

import live.turna.phenyl.bungee.BungeePhenyl
import live.turna.phenyl.common.config.Config
import live.turna.phenyl.common.message.messenger.AbstractMessenger
import live.turna.phenyl.common.utils.MessageUtils
import net.kyori.adventure.text.Component
import net.md_5.bungee.api.ProxyServer

class BungeeMessenger(plugin: BungeePhenyl) : AbstractMessenger<BungeePhenyl>(plugin) {
    override fun sendAllServer(message: String, force: Boolean) {
        val result = Component.text(altColor(message))
        if (force) {
            for (player in ProxyServer.getInstance().players) {
                // a player hasn't joined any server yet
                if (player.server == null) continue
                if (Config.enabled_servers.contains(player.server.info.name)) {
                    phenyl.getPlayer(player.uniqueId).sendMessage(result)
                }
            }
        } else sendAllServer(message)
    }

    override fun sendAllServer(message: String, exclude: Array<String>) {
        val result = Component.text(altColor(message))
        for (player in ProxyServer.getInstance().players) {
            // in case any player leaves the server while broadcasting messages
            if (player.server == null) continue
            if (Config.enabled_servers.contains(player.server.info.name)) {
                for (server in exclude) {
                    if (server != player.server.info.name) {
                        if (Config.nomessage_with_cross_server && MessageUtils(phenyl).isNoMessaged(player.uniqueId.toString())
                                .uuid() != null
                        ) continue
                        phenyl.getPlayer(player.uniqueId).sendMessage(result)
                    }
                }
            }
        }
    }

    override fun sendAllServer(message: Component) {
        for (player in ProxyServer.getInstance().players) {
            // in case any player leaves the server while broadcasting messages
            if (player.server == null) continue
            if (Config.enabled_servers.contains(player.server.info.name)) {
                if (MessageUtils(phenyl).isNoMessaged(player.uniqueId.toString()).uuid() != null) continue
                phenyl.getPlayer(player.uniqueId).sendMessage(message)
            }
        }
    }
}
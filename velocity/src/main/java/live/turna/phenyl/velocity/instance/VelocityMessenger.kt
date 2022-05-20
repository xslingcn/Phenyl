package live.turna.phenyl.velocity.instance

import live.turna.phenyl.common.config.Config
import live.turna.phenyl.common.message.messenger.AbstractMessenger
import live.turna.phenyl.common.utils.MessageUtils
import live.turna.phenyl.velocity.VelocityPhenyl
import net.kyori.adventure.text.Component

/**
 * **VelocityMessenger**
 *
 * @since 2022/5/21 2:02
 */
class VelocityMessenger(plugin: VelocityPhenyl) : AbstractMessenger<VelocityPhenyl>(plugin) {

    override fun sendAllServer(message: Component) {
        for (player in phenyl!!.loader.proxy.allPlayers) {
            if (player.currentServer.isEmpty) continue
            if (Config.enabled_servers.contains(player.currentServer.get().serverInfo.name)
                && MessageUtils(phenyl).isNoMessaged(player.uniqueId.toString()).uuid() == null
            ) phenyl.getPlayer(player.uniqueId).sendMessage(message)
        }
    }

    override fun sendAllServer(message: String, force: Boolean) {
        val result = Component.text(altColor(message))
        if (force) {
            for (player in phenyl!!.loader.proxy.allPlayers) {
                // a player hasn't joined any server yet
                if (player.currentServer.isEmpty) continue
                if (Config.enabled_servers.contains(player.currentServer.get().serverInfo.name)) {
                    phenyl.getPlayer(player.uniqueId).sendMessage(result)
                }
            }
        } else sendAllServer(message)
    }

    override fun sendAllServer(message: String, exclude: Array<String>) {
        val result = Component.text(altColor(message))
        for (player in phenyl!!.loader.proxy.allPlayers) {
            if (player.currentServer.isEmpty) continue
            if (Config.enabled_servers.contains(player.currentServer.get().serverInfo.name)) {
                for (server in exclude) {
                    if (server != player.currentServer.get().serverInfo.name) {
                        if (Config.nomessage_with_cross_server
                            && MessageUtils(phenyl).isNoMessaged(player.uniqueId.toString()).uuid() != null
                        ) continue
                        phenyl.getPlayer(player.uniqueId).sendMessage(result)
                    }
                }
            }
        }
    }
}
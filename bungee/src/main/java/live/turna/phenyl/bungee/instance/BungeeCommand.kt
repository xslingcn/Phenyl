package live.turna.phenyl.bungee.instance

import live.turna.phenyl.bungee.BungeePhenyl
import live.turna.phenyl.common.command.ServerCommandExecutor
import live.turna.phenyl.common.command.TabCompleter
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.plugin.Command
import net.md_5.bungee.api.plugin.TabExecutor

class BungeeCommand(private val phenyl: BungeePhenyl, name: String, alia: String?) :
    Command(name, "", alia), TabExecutor {
    override fun execute(sender: CommandSender, args: Array<String>) {
        try {
            ServerCommandExecutor(phenyl, phenyl.senderFactory.wrap(sender), args).match()
        } catch (e: RuntimeException) {
            phenyl.messenger.sendPlayer(e.message, phenyl.senderFactory.wrap(sender))
        }
    }

    override fun onTabComplete(sender: CommandSender, args: Array<String>): Iterable<String> {
        return TabCompleter(phenyl, phenyl.senderFactory.wrap(sender), args).onTabComplete()
    }
}
package live.turna.phenyl.velocity.instance

import com.velocitypowered.api.command.SimpleCommand
import live.turna.phenyl.common.command.ServerCommandExecutor
import live.turna.phenyl.common.command.TabCompleter
import live.turna.phenyl.velocity.VelocityPhenyl
import java.util.function.Consumer

/**
 * **VelocityCommand**
 *
 * @since 2022/5/21 2:31
 */
class VelocityCommand(private val phenyl: VelocityPhenyl) : SimpleCommand {
    override fun execute(invocation: SimpleCommand.Invocation) {
        try {
            ServerCommandExecutor(
                phenyl,
                phenyl.senderFactory.wrap(invocation.source()),
                invocation.arguments()
            ).match()
        } catch (e: IllegalArgumentException) {
            phenyl.messenger.sendPlayer(e.message, phenyl.senderFactory.wrap(invocation.source()))
        }
    }

    override fun suggest(invocation: SimpleCommand.Invocation): List<String> {
        val result: MutableList<String> = ArrayList()
        TabCompleter(
            phenyl,
            phenyl.senderFactory.wrap(invocation.source()),
            invocation.arguments()
        ).onTabComplete().forEach(Consumer { e: String -> result.add(e) })
        return result
    }
}
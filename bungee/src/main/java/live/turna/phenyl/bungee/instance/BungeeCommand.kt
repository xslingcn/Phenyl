package live.turna.phenyl.bungee.instance;

import live.turna.phenyl.bungee.BungeePhenyl;
import live.turna.phenyl.common.commands.ServerCommandExecutor;
import live.turna.phenyl.common.commands.TabCompleter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

/**
 * <b>BungeeCommand</b><br>
 * *
 *
 * @since 2022/5/9 5:47
 */
public class BungeeCommand extends Command implements TabExecutor {
    private final transient BungeePhenyl phenyl;

    public BungeeCommand(BungeePhenyl plugin, String name, String alia) {
        super(name, "", alia);
        phenyl = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        try {
            new ServerCommandExecutor<>(phenyl, phenyl.getSenderFactory().wrap(sender), args).match();
        } catch (RuntimeException e) {
            phenyl.getMessenger().sendPlayer(e.getMessage(), phenyl.getSenderFactory().wrap(sender));
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        return new TabCompleter<>(phenyl, phenyl.getSenderFactory().wrap(sender), args).onTabComplete();
    }
}
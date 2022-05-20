package live.turna.phenyl.common.command;

import live.turna.phenyl.common.database.Player;
import live.turna.phenyl.common.instance.PSender;
import live.turna.phenyl.common.plugin.AbstractPhenyl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <b>TabCompleter</b><br>
 * Phenyl tab-completion handler.
 *
 * @since 2022/5/9 5:16
 */
public class TabCompleter<P extends AbstractPhenyl, S extends PSender> {
    private final transient P phenyl;
    private final transient S sender;
    private final transient String[] args;

    public TabCompleter(P plugin, S sender, String[] args) {
        phenyl = plugin;
        this.sender = sender;
        this.args = args;
    }

    public Iterable<String> onTabComplete() {
        if (sender.isConsole()) return null;
        List<String> completions = new ArrayList<>();
        switch (args.length) {
            case 0 -> {
                Stream.of(ServerCommand.values()).filter(cmd -> sender.hasPermission(cmd.permission))
                        .forEach(cmd -> completions.add(cmd.prompt));
                return completions;
            }
            case 1 -> {
                Stream.of(ServerCommand.values()).filter(cmd -> sender.hasPermission(cmd.permission))
                        .forEach(cmd -> completions.add(cmd.prompt));
                return completions.stream().filter(val -> val.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
            }
            // player name completion
            case 2 -> {
                if (args[0].equals("mute")) {
                    List<PSender> playerList = phenyl.getPlayers().stream().filter(player -> player.getUsername().startsWith(args[1])).toList();
                    playerList.forEach(player -> completions.add(player.getUsername()));
                    return completions;
                }
                if (args[0].equals("at")) {
                    if (phenyl.getAllBoundPlayer().isEmpty()) return completions;
                    List<Player> playerList = phenyl.getAllBoundPlayer().stream().filter(player -> player.mcname().startsWith(args[1])).toList();
                    playerList.subList(0, Math.min(10, playerList.size())).forEach(player -> completions.add(player.mcname()));
                    return completions;
                }
            }
        }
        return completions;
    }
}
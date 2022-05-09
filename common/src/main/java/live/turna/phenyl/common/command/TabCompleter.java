package live.turna.phenyl.common.command;

import live.turna.phenyl.common.instance.PSender;
import live.turna.phenyl.common.plugin.AbstractPhenyl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <b>TabCompleter</b><br>
 * *
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
        if (sender.isConsole() || args.length == 0) return null;
        List<String> completions = new ArrayList<>();
        switch (args.length) {
            case 1 -> {
                Stream.of(ServerCommand.values()).filter(cmd -> sender.hasPermission(cmd.permission))
                        .forEach(cmd -> completions.add(cmd.prompt));
                return completions.stream().filter(val -> val.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
            }
            case 2 -> {
                if (args[0].equals("mute")) {
                    List<PSender> playerList = phenyl.getPlayers().stream().filter(player -> player.getUsername().startsWith(args[1])).toList();
                    playerList.forEach(player -> completions.add(player.getUsername()));
                    return completions;
                }
            }
        }
        return completions;
    }
}
package live.turna.phenyl;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * <b>PhenylCommand</b><br>
 * Command class with Logger and Phenyl instance.
 *
 * @since 2021/12/3 22:31
 */
public class PhenylCommand extends Command implements TabExecutor {
    public static final Logger LOGGER = LogManager.getLogger("PhenylMain");
    public static Phenyl phenyl = Phenyl.getInstance();

    public PhenylCommand(String name) {
        super(name);
    }

    public void execute(CommandSender sender, String[] args) {

    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
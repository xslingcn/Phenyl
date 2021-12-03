package live.turna.phenyl;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.util.logging.Logger;

/**
 * Command model with Logger and Phenyl instance.
 * @author xsling
 * @version 1.0
 * @since 2021/12/3 22:31
 */
public class PhenylCommand extends Command {
    public static final Logger LOGGER = Logger.getLogger("Phenyl");
    public static Phenyl phenyl = Phenyl.getInstance();

    /**
     * @param name Command name
     */
    public PhenylCommand(String name) {
        super(name);
    }

    public void execute(CommandSender sender, String[] args) {

    }
}
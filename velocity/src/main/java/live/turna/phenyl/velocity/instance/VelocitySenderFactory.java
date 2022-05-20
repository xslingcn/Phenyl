package live.turna.phenyl.velocity.instance;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import live.turna.phenyl.common.instance.AbstractSender;
import live.turna.phenyl.common.instance.PSender;
import live.turna.phenyl.common.instance.SenderFactory;
import live.turna.phenyl.velocity.VelocityPhenyl;
import net.kyori.adventure.text.Component;

import java.util.UUID;

/**
 * <b>VelocitySenderFactory</b></br>
 *
 * @since 2022/5/21 1:42
 */
public class VelocitySenderFactory extends SenderFactory<VelocityPhenyl, CommandSource> {
    private final VelocityPhenyl phenyl;

    public VelocitySenderFactory(VelocityPhenyl plugin) {
        super(plugin);
        phenyl = plugin;
    }

    @Override
    protected UUID getUniqueId(CommandSource sender) {
        if (sender instanceof Player player) return player.getUniqueId();
        return new UUID(0, 0);
    }

    @Override
    protected String getName(CommandSource sender) {
        if (sender instanceof Player player) return player.getUsername();
        return "CONSOLE";
    }

    @Override
    protected void sendMessage(CommandSource sender, Component message) {
        sender.sendMessage(message);
    }

    @Override
    protected Boolean hasPermission(CommandSource sender, String node) {
        return sender.hasPermission(node);
    }

    @Override
    protected Boolean isConsole(CommandSource sender) {
        return !(sender instanceof Player);
    }

    @Override
    protected String getServerName(CommandSource sender) {
        if (sender instanceof Player player) {
            if (player.getCurrentServer().isPresent())
                return player.getCurrentServer().get().getServerInfo().getName();
            return null;
        }
        return "CONSOLE";
    }

    @Override
    public PSender wrap(CommandSource sender) {
        if (sender == null) return null;
        return new AbstractSender<>(phenyl, this, sender);
    }
}
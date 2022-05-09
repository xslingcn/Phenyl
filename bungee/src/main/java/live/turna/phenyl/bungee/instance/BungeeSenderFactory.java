package live.turna.phenyl.bungee.instance;

import live.turna.phenyl.bungee.BungeePhenyl;
import live.turna.phenyl.common.instance.AbstractSender;
import live.turna.phenyl.common.instance.PSender;
import live.turna.phenyl.common.instance.SenderFactory;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Objects;
import java.util.UUID;

/**
 * <b>BungeeSenderFactory</b><br>
 *
 * @since 2022/5/3 13:12
 */
public class BungeeSenderFactory extends SenderFactory<BungeePhenyl, CommandSender> {
    private final BungeeAudiences audiences;

    public BungeeSenderFactory(BungeePhenyl plugin) {
        super(plugin);
        audiences = BungeeAudiences.create(plugin.getLoader());
    }

    @Override
    protected UUID getUniqueId(CommandSender sender) {
        if (sender instanceof ProxiedPlayer player)
            return player.getUniqueId();
        return new UUID(0, 0);
    }

    @Override
    protected String getName(CommandSender sender) {
        return sender.getName();
    }

    @Override
    protected void sendMessage(CommandSender sender, Component message) {
        audiences.sender(sender).sendMessage(message);
    }

    @Override
    protected Boolean hasPermission(CommandSender sender, String node) {
        return sender.hasPermission(node);
    }

    @Override
    protected Boolean isConsole(CommandSender sender) {
        return !(sender instanceof ProxiedPlayer);
    }

    @Override
    protected String getServerName(CommandSender sender) {
        if (sender instanceof ProxiedPlayer player)
            return player.getServer().getInfo().getName();
        return "CONSOLE";
    }

    @Override
    public PSender wrap(CommandSender sender) {
        Objects.requireNonNull(sender, "sender");
        return new AbstractSender<>(phenyl, this, sender);
    }
}
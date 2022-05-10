package live.turna.phenyl.common.instance;

import live.turna.phenyl.common.plugin.AbstractPhenyl;
import net.kyori.adventure.text.Component;

import java.util.UUID;

/**
 * <b>SenderFactory</b><br>
 * Factory to wrap a CommandSender to PSender.
 *
 * @see PSender
 * @since 2022/5/3 12:58
 */
public abstract class SenderFactory<P extends AbstractPhenyl, S> {
    protected final transient P phenyl;

    public SenderFactory(P plugin) {
        phenyl = plugin;
    }

    protected abstract UUID getUniqueId(S sender);

    protected abstract String getName(S sender);

    protected abstract void sendMessage(S sender, Component message);

    protected abstract Boolean hasPermission(S sender, String node);

    protected abstract Boolean isConsole(S sender);

    protected abstract String getServerName(S sender);

    public abstract PSender wrap(S sender);
}
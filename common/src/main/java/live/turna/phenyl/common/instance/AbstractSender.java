package live.turna.phenyl.common.instance;

import live.turna.phenyl.common.plugin.AbstractPhenyl;
import net.kyori.adventure.text.Component;

import java.util.UUID;

/**
 * <b>AbstractSender</b><br>
 * *
 *
 * @since 2022/5/3 13:04
 */
public class AbstractSender<S> implements PSender {
    protected final transient AbstractPhenyl phenyl;
    protected final transient SenderFactory<?, S> factory;
    protected final transient S sender;
    protected final transient UUID uuid;
    protected final transient String userName;

    public AbstractSender(AbstractPhenyl plugin, SenderFactory<?, S> factory, S sender) {
        phenyl = plugin;
        this.factory = factory;
        this.sender = sender;
        this.uuid = factory.getUniqueId(sender);
        this.userName = factory.getName(sender);
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public void sendMessage(Component message) {
        factory.sendMessage(sender, message);
    }

    @Override
    public Boolean hasPermission(String node) {
        return factory.hasPermission(sender, node);
    }

    @Override
    public String getServerName() {
        return factory.getServerName(sender);
    }

    @Override
    public Boolean isConsole() {
        return factory.isConsole(sender);
    }
}
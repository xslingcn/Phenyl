package live.turna.phenyl.bungee.listener.bungee;

import live.turna.phenyl.bungee.BungeePhenyl;
import live.turna.phenyl.common.eventhandler.server.OnChatEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * <b>OnBungeeChatEvent</b><br>
 * Called when player sent a chat message.
 * Forward the message to QQ group.<br>
 * Forward the message to all online players in other sub-servers if {@code cross_sever_format} is not set to "disabled".
 *
 * @since 2021/12/4 21:54
 */
public class BungeeOnChatEvent extends OnChatEvent<BungeePhenyl> implements Listener {
    public BungeeOnChatEvent(BungeePhenyl plugin) {
        super(plugin);
    }

    @EventHandler
    public void onChat(ChatEvent e) {
        if (e.isCommand() || e.isProxyCommand())
            return;
        super.fill(phenyl.getSenderFactory().wrap((ProxiedPlayer) e.getSender()), e.getMessage());
        super.handle();
    }
}
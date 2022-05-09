package live.turna.phenyl.bungee.listener.mirai;

import live.turna.phenyl.bungee.BungeePhenyl;
import live.turna.phenyl.bungee.event.BungeeGroupMessageEvent;
import live.turna.phenyl.common.eventhandler.mirai.OnGroupMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * <b>BungeeOnGroupMessageEvent</b><br>
 * Listener on GroupMessageEvent.<br>
 * This produces all group messages including commands and chats.
 *
 * @since 2021/12/4 18:55
 */
public class BungeeOnGroupMessageEvent extends OnGroupMessageEvent<BungeePhenyl> implements Listener {
    public BungeeOnGroupMessageEvent(BungeePhenyl plugin) {
        super(plugin);
    }

    @EventHandler
    public void onGroupMessage(BungeeGroupMessageEvent e) {
        super.fill(e.getGroup(), e.getSenderId(), e.getMessage(), e.getSenderNameCardOrNick());
        super.handle();
    }
}
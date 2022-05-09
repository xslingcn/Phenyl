package live.turna.phenyl.bungee.instance;

import live.turna.phenyl.bungee.BungeePhenyl;
import live.turna.phenyl.common.config.Config;
import live.turna.phenyl.common.message.messenger.AbstractMessenger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * <b>BungeeMessenger</b><br>
 * *
 *
 * @since 2022/5/3 14:14
 */
public class BungeeMessenger extends AbstractMessenger<BungeePhenyl> {

    public BungeeMessenger(BungeePhenyl plugin) {
        super(plugin);

    }

    @Override
    public void sendAllServer(String message, Boolean force) {
        TextComponent result = Component.text(altColor(message));
        if (force) {
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                // a player hasn't joined any server yet
                if (player.getServer() == null) continue;

                if (Config.enabled_servers.contains(player.getServer().getInfo().getName())) {
                    phenyl.getPlayer(player.getUniqueId()).sendMessage(result);
                }
            }
        } else sendAllServer(message);
    }


    @Override
    public void sendAllServer(String message, String[] exclude) {
        TextComponent result = Component.text(altColor(message));
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            // in case any player leaves the server while broadcasting messages
            if (player.getServer() == null) continue;

            if (Config.enabled_servers.contains(player.getServer().getInfo().getName())) {
                for (String server : exclude) {
                    if (!server.equals(player.getServer().getInfo().getName())) {
                        if (Config.nomessage_with_cross_server && getNoMessage(player.getUniqueId().toString()).uuid() != null)
                            continue;
                        phenyl.getPlayer(player.getUniqueId()).sendMessage(result);
                    }
                }
            }
        }
    }

    @Override
    public void sendAllServer(Component message) {
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            // in case any player leaves the server while broadcasting messages
            if (player.getServer() == null) continue;

            if (Config.enabled_servers.contains(player.getServer().getInfo().getName())) {
                if (getNoMessage(player.getUniqueId().toString()).uuid() != null) continue;
                phenyl.getPlayer(player.getUniqueId()).sendMessage(message);
            }
        }
    }
}
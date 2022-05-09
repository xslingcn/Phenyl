package live.turna.phenyl.common.eventhandler.server;

import live.turna.phenyl.common.config.Config;
import live.turna.phenyl.common.instance.PSender;
import live.turna.phenyl.common.plugin.AbstractPhenyl;
import live.turna.phenyl.common.utils.MessageUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * <b>OnBungeeChatEvent</b><br>
 * Called when player sent a chat message.
 * Forward the message to QQ group.<br>
 * Forward the message to all online players in other sub-servers if {@code cross_sever_format} is not set to "disabled".
 *
 * @since 2021/12/4 21:54
 */
public abstract class OnChatEvent<P extends AbstractPhenyl> {
    protected transient P phenyl;
    protected transient MessageUtils messageUtils;

    private transient PSender player;
    private transient String message;

    public OnChatEvent(P plugin) {
        phenyl = plugin;
        messageUtils = new MessageUtils(phenyl);
    }

    public void fill(PSender sender, String message) {
        player = sender;
        this.message = message;
    }

    public void handle() {
        switch (Config.forward_mode) {
            // always forward message from in-game players.
            case "sync", "bind" -> CompletableFuture.supplyAsync(() -> phenyl.getForwarder().forwardToQQ(message, player.getUsername(), player.getUUID().toString(), messageUtils.getServerName(player.getServerName()))).orTimeout(3, TimeUnit.SECONDS);
        }

        if (!Config.cross_sever_format.equals("disabled")) {
            if (messageUtils.isMuted(player.getUUID().toString()).uuid() != null) return;
            String format = Config.cross_sever_format
                    .replace("%sub_server%", messageUtils.getServerName((player.getServerName())))
                    .replace("%username%", player.getUsername())
                    .replace("%message%", message);
            CompletableFuture.supplyAsync(() -> {
                phenyl.getMessenger().sendAllServer(format, new String[]{player.getServerName()});
                return true;
            }).orTimeout(3, TimeUnit.SECONDS);
        }
    }
}
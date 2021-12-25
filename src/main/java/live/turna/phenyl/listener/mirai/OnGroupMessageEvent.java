package live.turna.phenyl.listener.mirai;

import live.turna.phenyl.PhenylListener;
import live.turna.phenyl.config.PhenylConfiguration;
import live.turna.phenyl.database.Database;
import live.turna.phenyl.mirai.event.CGroupMessageEvent;

import static live.turna.phenyl.message.Forward.forwardToBungee;
import static live.turna.phenyl.message.I18n.i18n;
import static live.turna.phenyl.utils.Bind.*;
import static live.turna.phenyl.utils.Mirai.sendGroup;
import static live.turna.phenyl.utils.Message.getServerName;
import static live.turna.phenyl.bind.BindHandler.handleRequest;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.QuoteReply;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <b>OnGroupMessageEvent</b><br>
 * Listener on GroupMessageEvent.<br>
 * This produces all group messages including commands and chats.
 *
 * @since 2021/12/4 18:55
 */
public class OnGroupMessageEvent extends PhenylListener {
    private transient Group group;
    private transient Long senderID;
    private transient MessageChain message;
    private transient String messageString;
    private transient CGroupMessageEvent event;

    @EventHandler
    public void onGroupMessage(CGroupMessageEvent e) {

        event = e;
        group = event.getGroup();
        senderID = event.getSenderID();
        message = event.getMessage();
        messageString = event.getMessageString();

        if (group == null || senderID == null || message == null || messageString.isEmpty()) return;
        if (!PhenylConfiguration.enabled_groups.contains(group.getId())) return;

        // Message is a command.
        if (messageString.startsWith(PhenylConfiguration.command_prefix)) {
            String command = messageString.substring(PhenylConfiguration.command_prefix.length());
            CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
                try {
                    handleCommand(command);
                    return true;
                } catch (IllegalArgumentException ex) {
                    MessageChain reply = new MessageChainBuilder()
                            .append(new QuoteReply(message))
                            .append(ex.getMessage())
                            .build();
                    sendGroup(group, reply);
                    if (PhenylConfiguration.debug) ex.printStackTrace();
                    return false;
                }
            }).orTimeout(3, TimeUnit.SECONDS);
            return;
        }
        // Random message.
        CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
            switch (PhenylConfiguration.forward_mode) {
                case "sync" -> forwardToBungee(group, senderID, messageString, event.getSenderNameCardOrNick(), null);
                case "bind" -> {
                    String userName = Database.getBinding(senderID).mcname();
                    // Check if is bound.
                    if (userName != null)
                        forwardToBungee(group, senderID, messageString, event.getSenderNameCardOrNick(), userName);
                }
                default -> {
                    if (PhenylConfiguration.debug) LOGGER.error(i18n("invalidForward"));
                    return false;
                }
            }
            return true;
        }).orTimeout(3, TimeUnit.SECONDS);
    }

    /**
     * Handle all message with <i>command_prefix</i> mark.
     *
     * @param command The command message content.
     * @throws IllegalArgumentException commandNotFound: Command not found.
     * @throws IllegalArgumentException invalidUserName: Provided username isn't a valid Minecraft username.
     * @throws IllegalArgumentException illegalArgument: Command arguments are illegal.
     */
    private void handleCommand(String command) throws IllegalArgumentException {
        String[] args = command.split(" ");

        //Binding requests.
        if (args[0].equals(PhenylConfiguration.bind_command)) {
            if (args.length != 2) throw new IllegalArgumentException(i18n("illegalArgument"));
            if (!isValidUsername(args[1])) throw new IllegalArgumentException(i18n("invalidUserName"));
            String userName = args[1];
            String code = handleRequest(userName, senderID);
            MessageChain reply = new MessageChainBuilder()
                    .append(new QuoteReply(message))
                    .append(i18n("completeBindInGame"))
                    .build();
            sendGroup(group, reply);
            sendGroup(group, "/phenyl verify " + code);
            return;
        }

        //Confirmation requests.
        if (args[0].equals(PhenylConfiguration.confirm_command)) {
            if (args.length != 2) throw new IllegalArgumentException(i18n("illegalArgument"));
            MessageChain reply = new MessageChainBuilder()
                    .append(new QuoteReply(message))
                    .append(verifier(senderID, args[1]))
                    .build();
            sendGroup(group, reply);
            return;
        }

        // get online list
        if (args[0].equals(PhenylConfiguration.online_command)) {
            AtomicInteger onlineCount = new AtomicInteger();
            onlineCount.getAndSet(0);
            HashMap<String, String> result = new HashMap<>();
            ProxyServer.getInstance().getServers().forEach((s, serverInfo) -> {
                StringBuilder players = new StringBuilder();
                serverInfo.getPlayers().forEach(player -> {
                    players.append(player.getName()).append(",");
                    onlineCount.getAndIncrement();
                });
                if (players.isEmpty()) return;
                result.put(getServerName(serverInfo), players.substring(0, players.length() - 1));
            });

            String totalFormat = PhenylConfiguration.online_total_format
                    .replace("%player_count%", ((Integer) onlineCount.get()).toString());
            List<String> listFormat = new ArrayList<>();
            result.forEach((key, value) -> listFormat.add(PhenylConfiguration.online_list_format
                    .replace("%sub_server%", key)
                    .replace("%username%", value)));
            MessageChainBuilder reply = new MessageChainBuilder()
                    .append(new QuoteReply(message))
                    .append(totalFormat)
                    .append("\n");
            listFormat.forEach(reply::append);
            sendGroup(group, reply.build());
            return;
        }

        //Random message that needs to be forwarded in *command* mode.
        if (PhenylConfiguration.forward_mode.equalsIgnoreCase("command")) {
            String userName = Database.getBinding(senderID).mcname();
            if (userName == null) throw new IllegalArgumentException(i18n("notBoundYet"));
            forwardToBungee(group, senderID, messageString.substring(1), event.getSenderNameCardOrNick(), userName);
            return;
        }
        throw new IllegalArgumentException(i18n("commandNotFound"));
    }

}
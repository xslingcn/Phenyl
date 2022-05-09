package live.turna.phenyl.common.eventhandler.mirai;

import live.turna.phenyl.common.command.GroupCommandExecutor;
import live.turna.phenyl.common.config.Config;
import live.turna.phenyl.common.plugin.AbstractPhenyl;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static live.turna.phenyl.common.message.I18n.i18n;

/**
 * <b>OnGroupMessageEvent</b><br>
 * Listener on GroupMessageEvent.<br>
 * This produces all group messages including commands and chats.
 *
 * @since 2021/12/4 18:55
 */
public abstract class OnGroupMessageEvent<P extends AbstractPhenyl> {
    private final transient P phenyl;
    private transient Group group;
    private transient Long senderId;
    private transient MessageChain message;
    private transient String messageString;
    private transient String nameCardOrNick;
    private transient List<SingleMessage> images;

    public OnGroupMessageEvent(P plugin) {
        phenyl = plugin;
    }

    public void fill(Group group, Long senderID, MessageChain message, String nameCardOrNick) {
        this.group = group;
        this.senderId = senderID;
        this.message = message;
        this.nameCardOrNick = nameCardOrNick;
    }

    public void handle() {
        messageString = message.contentToString();

        if (group == null || senderId == null || message == null || messageString.isEmpty()) return;
        if (!Config.enabled_groups.contains(group.getId())) return;
        images = message.stream().filter(Image.class::isInstance).collect(Collectors.toList());

        // message with a command prefix
        if (messageString.startsWith(Config.command_prefix)) {
            String command = messageString.substring(Config.command_prefix.length());
            CompletableFuture.supplyAsync(() -> {
                try {
                    if (!executeCommand(command)) {
                        // if is random message that needs to be forwarded under `command` mode
                        String userName = phenyl.getStorage().getBinding(senderId).mcname();
                        if (userName == null) throw new IllegalArgumentException(i18n("notBoundYet"));
                        phenyl.getForwarder().forwardToServer(group, senderId, messageString.substring(1), nameCardOrNick, null);
                    }
                    return true;
                } catch (IllegalArgumentException ex) {
                    MessageChain reply = new MessageChainBuilder()
                            .append(new QuoteReply(message))
                            .append(ex.getMessage())
                            .build();
                    phenyl.getMessenger().sendGroup(group, reply);
                    if (Config.debug) ex.printStackTrace();
                    return false;
                }
            }).orTimeout(3, TimeUnit.SECONDS);
            return;
        }

        // random message
        CompletableFuture.supplyAsync(() -> {
            switch (Config.forward_mode) {
                case "sync", "bind" -> phenyl.getForwarder().forwardToServer(group, senderId, message, nameCardOrNick, images);
                default -> {
                    return false;
                }
            }
            return true;
        }).orTimeout(3, TimeUnit.SECONDS);
    }

    private boolean executeCommand(String command) throws IllegalArgumentException {
        String[] args = command.split(" ");

        return new GroupCommandExecutor<>(phenyl, group, senderId, message, args).match();
    }
}
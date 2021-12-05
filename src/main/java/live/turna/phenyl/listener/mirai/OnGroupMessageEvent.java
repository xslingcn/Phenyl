package live.turna.phenyl.listener.mirai;

import live.turna.phenyl.PhenylListener;
import live.turna.phenyl.config.PhenylConfiguration;
import live.turna.phenyl.mirai.event.CGroupMessageEvent;

import static live.turna.phenyl.bind.BindHandler.handleConfirm;
import static live.turna.phenyl.message.I18n.i18n;
import static live.turna.phenyl.utils.Bind.isValidUsername;
import static live.turna.phenyl.utils.Bind.isValidVerificationCode;
import static live.turna.phenyl.utils.Mirai.sendGroup;
import static live.turna.phenyl.bind.BindHandler.handleRequest;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.QuoteReply;

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

    @EventHandler
    public void onGroupMessage(CGroupMessageEvent event) {
        group = event.getGroup();
        senderID = event.getSenderID();
        message = event.getMessage();
        if (group == null || senderID == null || message == null) return;

        if (!PhenylConfiguration.enabled_groups.contains(group.getId())) return;
        String messageString = event.getMessageString();
        if (messageString.startsWith(PhenylConfiguration.command_prefix)) {
            try {
                String command = messageString.substring(PhenylConfiguration.command_prefix.length() - 1, messageString.length() - 1);
                handleCommand(command);
            } catch (IllegalArgumentException e) {
                sendGroup(group, e.getMessage());
            }
        }
    }

    /**
     * Handle all message with <i>command_prefix</i> mark.
     *
     * @param command The command message content.
     * @throws IllegalArgumentException commandNotFound: Command not found.
     * @throws IllegalArgumentException invalidCode: Verification code is neither format-correct nor valid.
     * @throws IllegalArgumentException invalidUserName: Provided username isn't a valid Minecraft username.
     * @throws IllegalArgumentException illegalArgument: Command arguments are illegal.
     */
    private void handleCommand(String command) throws IllegalArgumentException {
        String[] args = command.split(" ");
        if (args[0].equals(PhenylConfiguration.bind_command)) {
            if (args.length == 2) {
                if (!isValidUsername(args[1])) throw new IllegalArgumentException(i18n("invalidUserName"));
                String userName = args[1];
                String code = handleRequest(userName, senderID);
                MessageChain reply = new MessageChainBuilder()
                        .append(new QuoteReply(message))
                        .append(i18n("completeBind", code))
                        .build();
                sendGroup(group, reply);
            } else throw new IllegalArgumentException(i18n("illegalArgument"));
        } else if (args[0].equals(PhenylConfiguration.confirm_command)) {
            if (args.length == 2) {
                if (isValidVerificationCode(args[1], PhenylConfiguration.verification)) {
                    String code = args[1];
                    String userName = handleConfirm(senderID, code);
                    if (userName != null) {
                        MessageChain reply = new MessageChainBuilder()
                                .append(new QuoteReply(message))
                                .append(i18n("bindSuccess", userName))
                                .build();
                        sendGroup(group, reply);
                    } else throw new IllegalArgumentException(i18n("invalidCode"));
                } else throw new IllegalArgumentException(i18n("invalidCode"));
            } else throw new IllegalArgumentException(i18n("illegalArgument"));
        } else throw new IllegalArgumentException(i18n("commandNotFound"));
    }


}
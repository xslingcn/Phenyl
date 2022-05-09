package live.turna.phenyl.common.commands;

import live.turna.phenyl.common.config.Config;
import live.turna.phenyl.common.plugin.AbstractPhenyl;
import live.turna.phenyl.common.utils.BindUtils;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.QuoteReply;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static live.turna.phenyl.common.message.I18n.i18n;

/**
 * <b>GroupCommandExecutor</b><br>
 * *
 *
 * @since 2022/5/3 23:46
 */
public class GroupCommandExecutor<P extends AbstractPhenyl> {
    private final transient P phenyl;
    private final transient Group group;
    private final transient Long senderId;
    private final transient MessageChain message;
    private final transient String[] args;

    public GroupCommandExecutor(P plugin, Group group, Long senderId, MessageChain message, String[] args) {
        phenyl = plugin;
        this.group = group;
        this.senderId = senderId;
        this.message = message;
        this.args = args;
    }

    public boolean match() throws IllegalArgumentException {
        AtomicBoolean isCommand = new AtomicBoolean(true);
        List<GroupCommand> match = Stream.of(GroupCommand.values())
                .filter(cmd -> cmd.prompt.equals(args[0])).toList();
        if (match.isEmpty() && !Config.forward_mode.equals("command"))
            throw new IllegalArgumentException(i18n("commandNotFound"));
        match.forEach(cmd -> {
            if (args.length != cmd.argCount) throw new IllegalArgumentException(i18n("illegalArgument"));
            if (cmd.prompt.equals(Config.bind_command)) bind();
            else if (cmd.prompt.equals(Config.confirm_command)) confirm();
            else if (cmd.prompt.equals(Config.online_command)) online();
            else if (cmd.prompt.equals(Config.status_command)) status();
            else isCommand.set(false);
        });
        return isCommand.get();
    }

    public void bind() throws IllegalArgumentException {
        if (!BindUtils.isValidUsername(args[1])) throw new IllegalArgumentException(i18n("invalidUserName"));
        String userName = args[1];
        String code = phenyl.getBindHandler().handleRequest(userName, senderId);
        MessageChain reply = new MessageChainBuilder()
                .append(new QuoteReply(message))
                .append(i18n("completeBindInGame"))
                .build();
        phenyl.getMessenger().sendGroup(group, reply);
        phenyl.getMessenger().sendGroup(group, "/phenyl verify " + code);
    }

    public void confirm() throws IllegalArgumentException {
        String code = args[1];
        MessageChain reply = new MessageChainBuilder()
                .append(new QuoteReply(message))
                .append(phenyl.getBindHandler().handleConfirm(senderId, code))
                .build();
        phenyl.getMessenger().sendGroup(group, reply);
    }

    public void online() throws IllegalArgumentException {
        String totalFormat = Config.online_total_format
                .replace("%player_count%", phenyl.getOnlineCount().toString());
        List<String> listFormat = new ArrayList<>();
        phenyl.getOnlineList().forEach((key, value) -> listFormat.add(Config.online_list_format
                .replace("%sub_server%", key)
                .replace("%username%", value)));
        MessageChainBuilder reply = new MessageChainBuilder()
                .append(new QuoteReply(message));
        StringBuilder listContent = new StringBuilder();
        listFormat.forEach(listContent::append);
        phenyl.getMessenger().sendGroup(group, reply.append((totalFormat + "\n" + listContent).trim()).build());
    }

    public void status() throws IllegalArgumentException {
        List<String> listFormat = new ArrayList<>();
        phenyl.getStatus().forEach((serverName, status) -> listFormat.add(serverName + " - " + "[" + (status ? "√" : "×") + "]" + "\n"));
        MessageChainBuilder reply = new MessageChainBuilder()
                .append(new QuoteReply(message));
        StringBuilder listContent = new StringBuilder();
        listFormat.forEach(listContent::append);
        phenyl.getMessenger().sendGroup(group, reply.append(listContent.toString().trim()).build());
    }

}
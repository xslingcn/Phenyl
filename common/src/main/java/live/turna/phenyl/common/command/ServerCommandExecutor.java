package live.turna.phenyl.common.command;

import live.turna.phenyl.common.config.Config;
import live.turna.phenyl.common.database.Player;
import live.turna.phenyl.common.instance.PSender;
import live.turna.phenyl.common.mirai.MiraiLoginSolver;
import live.turna.phenyl.common.plugin.AbstractPhenyl;
import live.turna.phenyl.common.utils.BindUtils;
import live.turna.phenyl.common.utils.MessageUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static live.turna.phenyl.common.message.I18n.i18n;

/**
 * <b>ServerCommandExecutor</b><br>
 * Command executor for those called in game.
 *
 * @since 2022/5/6 17:34
 */
public class ServerCommandExecutor<P extends AbstractPhenyl, S extends PSender> {
    private final transient P phenyl;
    private final transient Logger LOGGER;
    private final transient S sender;
    private final transient String[] args;

    public ServerCommandExecutor(P plugin, S sender, String[] args) {
        phenyl = plugin;
        LOGGER = phenyl.getLogger();
        this.sender = sender;
        this.args = args;
    }

    /**
     * Match and perform the commands.
     *
     * @throws RuntimeException The failing message. <br/>
     *                          1). commandNotFoundPhenyl: Unknown command. Could be player-only commands called by console.<br/>
     *                          2). noPermission: The command sender doesn't have the permission to perform it.<br/>
     *                          3). illegalArgumentPhenyl: The number of arguments doesn't match.
     */
    public void match() throws RuntimeException {
        if (args.length == 0) {
            welcome();
            return;
        }
        List<ServerCommand> match = Stream.of(ServerCommand.values())
                .filter(cmd -> cmd.prompt.equals(args[0]))
                .toList();
        if (match.isEmpty()) throw new RuntimeException(i18n("commandNotFoundPhenyl"));
        else match.forEach(cmd -> {
            if (sender.isConsole())
                switch (args[0].toLowerCase()) {
                    case "bind", "verify", "say", "nomessage", "at" -> throw new RuntimeException(i18n("commandNotFoundPhenyl"));
                }
            if (!sender.hasPermission(cmd.permission)) throw new RuntimeException(i18n("noPermission"));
            if (!cmd.argCnt.equals(args.length))
                throw new RuntimeException(i18n("illegalArgumentPhenyl"));
            else {
                switch (args[0].toLowerCase()) {
                    case "bind" -> bind();
                    case "verify" -> verify();
                    case "say" -> say();
                    case "nomessage" -> noMessage();
                    case "at" -> at();
                }
            }
            switch (args[0]) {
                case "help" -> help();
                case "reload" -> reload();
                case "slider" -> slider();
                case "login" -> login();
                case "logout" -> logout();
                case "mute" -> mute();
            }
        });
    }

    private void welcome() {
        String message = i18n("welcomeVersionInfo", phenyl.getVersion());
        phenyl.getMessenger().sendPlayer(message, sender);
    }

    /**
     * {@code /phenyl help} <br/>
     * Send help message, the content varies regarding roles and permissions.
     */
    private void help() {
        String message = i18n("welcomeMessage", phenyl.getVersion()) + "\n"
                + i18n("helpMessage") + "\n"
                + i18n("commandHelp") + "\n";
        if (!sender.isConsole()) message += i18n("commandHelpPlayer") + "\n";
        if (sender.hasPermission("phenyl.admin.reload")) message += i18n("commandHelpAdmin");
        phenyl.getMessenger().sendPlayer(message, sender);
    }

    /**
     * {@code /phenyl reload} <br/>
     * Reload Phenyl. <br/>
     * Would do: reload config, reconnect database, unregister and then register all listeners, re-login Mirai account.
     */
    private void reload() {
        if (phenyl.reload())
            phenyl.getMessenger().sendPlayer(i18n("reloadSuccessful", phenyl.getVersion()), sender);
    }

    /**
     * {@code /phenyl slider [TICKET]} <br/>
     * Add ticket to solving queue.
     */
    private void slider() {
        MiraiLoginSolver.addTicket(args[1]);
    }

    /**
     * {@code /phenyl login} <br/>
     * Attempt to log the bot in.
     */
    private void login() {
        try {
            CompletableFuture.supplyAsync(() -> phenyl.getMirai().logIn())
                    .thenAccept((result) -> phenyl.getMessenger().sendPlayer(result ?
                            i18n("logInSuccess", phenyl.getMirai().getBot().getNick()) :
                            i18n("alreadyLoggedIn", String.valueOf(phenyl.getMirai().getBot().getId())), sender));
        } catch (Exception e) {
            LOGGER.error(i18n("logInFail", e.getLocalizedMessage()));
            if (Config.debug) e.printStackTrace();
        }
    }

    /**
     * {@code /phenyl logout} <br/>
     * Attempt to log the bot out.
     */
    private void logout() {
        String message;
        try {
            if (phenyl.getMirai().logOut())
                message = i18n("logOutSuccess", phenyl.getMirai().getBot().getNick());
            else message = i18n("yetLoggedIn");
            phenyl.getMessenger().sendPlayer(message, sender);
        } catch (Exception e) {
            LOGGER.warn(i18n("logOutFail", e.getLocalizedMessage()));
            if (Config.debug) e.printStackTrace();
        }
    }

    /**
     * {@code /phenyl mute [USERNAME]} <br/>
     * Mute someone, so that messages would not be forwarded.
     */
    private void mute() {
        PSender target = phenyl.getPlayer(args[1]);
        if (target == null) {
            phenyl.getMessenger().sendPlayer(i18n("playerNotFound", args[1]), sender);
            return;
        }
        // check if the player is already muted
        Player muted = new MessageUtils(phenyl).isMuted(target.getUUID().toString());
        if (muted.uuid() != null) {
            phenyl.getStorage().updateMutedPlayer(muted.uuid(), false);
            phenyl.getMutedPlayer().remove(muted);
            phenyl.getMessenger().sendPlayer(i18n("unMutedPlayer", args[1]), sender);
        } else {
            phenyl.getStorage().updateMutedPlayer(target.getUUID().toString(), true);
            phenyl.getMutedPlayer().add(phenyl.getStorage().getBinding(target.getUUID().toString()));
            phenyl.getMessenger().sendPlayer(i18n("mutedPlayer", args[1]), sender);
        }
    }

    /**
     * {@code /phenyl mute [QQID]} <br/>
     * Request to bind to a QQ account.
     */
    private void bind() {
        if (!BindUtils.isValidQQID(args[1])) {
            phenyl.getMessenger().sendPlayer(i18n("invalidQQID"), sender);
            return;
        }

        String code = phenyl.getBindHandler().handleRequest(sender.getUsername(), Long.parseLong(args[1]));
        phenyl.getMessenger().sendPlayer(i18n("completeBindInGroup"), sender);
        String codePrompt = Config.command_prefix + Config.confirm_command + " " + code;
        TextComponent bind = Component.text(codePrompt, NamedTextColor.YELLOW)
                .clickEvent(ClickEvent.copyToClipboard(codePrompt))
                .hoverEvent(HoverEvent.showText(Component.text(i18n("clickToCopy"), NamedTextColor.YELLOW)));

        phenyl.getMessenger().sendPlayer(bind, sender);
    }

    /**
     * {@code /phenyl verify [CODE]} <br/>
     * Verify a binding request.
     */
    private void verify() {
        TextComponent message;
        try {
            message = Component.text(phenyl.getBindHandler().handleConfirm(sender.getUsername(), args[1]), NamedTextColor.DARK_AQUA);
        } catch (IllegalArgumentException e) {
            message = Component.text(e.getMessage(), NamedTextColor.DARK_RED);
        }
        phenyl.getMessenger().sendPlayer(message, sender);
    }

    /**
     * {@code /phenyl say [MESSAGE]} <br/>
     * Send message to groups under {@code command} mode.
     */
    private void say() {
        if (Config.forward_mode.equals("command")) {
            CompletableFuture.supplyAsync(() ->
                    phenyl.getForwarder().forwardToQQ(
                            args[1], sender.getUsername(), sender.getUUID().toString(), new MessageUtils(phenyl).getServerName(sender.getServerName())
                    )).orTimeout(3, TimeUnit.SECONDS);
        } else phenyl.getMessenger().sendPlayer(i18n("notCommandMode"), sender);
    }

    /**
     * {@code /phenyl nomessage} <br/>
     * Toggle whether to receive messages from groups.
     */
    private void noMessage() {
        // check if the player is already nomessaged
        Player noMessaged = new MessageUtils(phenyl).isNoMessaged(sender.getUUID().toString());
        if (noMessaged.uuid() != null) {
            phenyl.getStorage().updateNoMessagePlayer(noMessaged.uuid(), false);
            phenyl.getNoMessagePlayer().remove(noMessaged);
            phenyl.getMessenger().sendPlayer(i18n("receiveMessage"), sender);
        } else {
            phenyl.getStorage().updateNoMessagePlayer(sender.getUUID().toString(), true);
            phenyl.getNoMessagePlayer().add(phenyl.getStorage().getBinding(sender.getUUID().toString()));
            phenyl.getMessenger().sendPlayer(i18n("noMessage"), sender);
        }
    }

    private void at() {
        List<Player> playerList = phenyl.getAllBoundPlayer();
        if (playerList.isEmpty()) throw new RuntimeException(i18n("boundPlayerNotFound"));
        if (playerList.stream().noneMatch(player -> player.mcname().equals(args[1])))
            throw new RuntimeException(i18n("boundPlayerNotFound"));
        Player targetPlayer = playerList.stream().filter(player -> player.mcname().equals(args[1])).findFirst().get();
        // get the pattern before and after %message%
        String[] format = Config.server_to_qq_format
                .replace("%sub_server%", new MessageUtils(phenyl).getServerName(sender.getServerName()))
                .replace("%username%", sender.getUsername())
                .split("%message%");
        for (Long id : Config.enabled_groups) {
            try {
                Group group = phenyl.getMirai().getBot().getGroupOrFail(id);
                Member target = group.getOrFail(targetPlayer.qqid());
                MessageChain message = new MessageChainBuilder()
                        .append(format[0])
                        .append(new At(target.getId()))
                        .append(format.length > 1 ? format[1] : "")
                        .build();
                phenyl.getMessenger().sendGroup(group, message);
                phenyl.getMessenger().sendPlayer(i18n("atSent", group.getName(), target.getNameCard().isEmpty() ? target.getNick() : target.getNameCard()), sender);
            } catch (NoSuchElementException ignored) {
            }
        }
    }
}
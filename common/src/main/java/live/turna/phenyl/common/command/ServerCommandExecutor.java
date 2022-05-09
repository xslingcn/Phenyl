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
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static live.turna.phenyl.common.message.I18n.i18n;

/**
 * <b>ServerCommandExecutor</b><br>
 * *
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
                    case "bind", "verify", "say", "nomessage" -> throw new RuntimeException(i18n("commandNotFoundPhenyl"));
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

    private void help() {
        String message = i18n("welcomeMessage", phenyl.getVersion()) + "\n"
                + i18n("helpMessage") + "\n"
                + i18n("commandHelp") + "\n";
        if (!sender.isConsole()) message += i18n("commandHelpPlayer");
        if (sender.hasPermission("phenyl.admin.reload")) message += i18n("commandHelpAdmin");
        phenyl.getMessenger().sendPlayer(message, sender);
    }

    private void reload() {
        if (phenyl.reload())
            phenyl.getMessenger().sendPlayer(i18n("reloadSuccessful", phenyl.getVersion()), sender);
    }

    private void slider() {
        MiraiLoginSolver.addTicket(args[1]);
    }

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

    private void mute() {
        PSender target = phenyl.getPlayer(args[1]);
        if (target == null) {
            phenyl.getMessenger().sendPlayer(i18n("playerNotFound", args[1]), sender);
            return;
        }
        // check if the player is already muted
        Player muted = phenyl.getMessenger().getMuted(target.getUUID().toString());
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

    private void verify() {
        TextComponent message;
        try {
            message = Component.text(phenyl.getBindHandler().handleConfirm(sender.getUsername(), args[1]), NamedTextColor.DARK_AQUA);
        } catch (IllegalArgumentException e) {
            message = Component.text(e.getMessage(), NamedTextColor.DARK_RED);
        }
        phenyl.getMessenger().sendPlayer(message, sender);
    }

    private void say() {
        if (Config.forward_mode.equals("command")) {
            CompletableFuture.supplyAsync(() ->
                    phenyl.getForwarder().forwardToQQ(
                            args[1], sender.getUsername(), sender.getUUID().toString(), new MessageUtils(phenyl).getServerName(sender.getServerName())
                    )).orTimeout(3, TimeUnit.SECONDS);
        } else phenyl.getMessenger().sendPlayer(i18n("notCommandMode"), sender);
    }

    private void noMessage() {
        // check if the player is already nomessaged
        Player noMessaged = phenyl.getMessenger().getNoMessage(sender.getUUID().toString());
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
}
package live.turna.phenyl.commands;

import live.turna.phenyl.Phenyl;
import live.turna.phenyl.PhenylCommand;

import static live.turna.phenyl.message.I18n.i18n;
import static live.turna.phenyl.message.Forward.forwardToQQ;
import static live.turna.phenyl.utils.Bind.verifier;
import static live.turna.phenyl.utils.Message.getServerName;
import static live.turna.phenyl.utils.Message.sendMessage;
import static live.turna.phenyl.bind.BindHandler.handleRequest;

import live.turna.phenyl.config.PhenylConfiguration;
import live.turna.phenyl.database.Database;
import live.turna.phenyl.database.Player;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * <b>CommandHandler</b><br>
 * CommandHandler of Phenyl.
 *
 * @since 2021/12/3 4:28
 */
public class CommandHandler extends PhenylCommand {

    /**
     * @param name Command name
     */
    public CommandHandler(String name) {
        super(name);
    }

    /**
     * Execute commands.
     *
     * @param sender Command sender.
     * @param args   Command arguments.
     */
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer || sender == ProxyServer.getInstance().getConsole())) return;
        if (args.length == 0) {
            sendMessage(i18n("welcomeVersionInfo", phenyl.getDescription().getVersion()), sender);
            return;
        }
        // Commands executable for both CONSOLE and players.
        switch (args[0].toLowerCase()) {
            case "help" -> {
                sendMessage(i18n("welcomeMessage", phenyl.getDescription().getVersion()), sender);
                sendMessage(i18n("helpMessage"), sender);
                sendMessage(i18n("commandHelp"), sender);
            }
            case "reload" -> {
                if (sender.hasPermission("phenyl.admin.reload")) {
                    if (args.length == 1) {
                        phenyl.reload();
                        sendMessage(i18n("reloadSuccessful", phenyl.getDescription().getVersion()), sender);
                    } else sendMessage(i18n("illegalArgumentPhenyl"), sender);
                } else sendMessage(i18n("noPermission"), sender);
            }
            case "login" -> {
                if (sender.hasPermission("phenyl.admin.login")) {
                    if (args.length == 1) {
                        try {
                            if (miraiInstance.logIn())
                                sendMessage(i18n("logInSuccess", miraiInstance.getBot().getNick()), sender);
                            else
                                sendMessage(i18n("alreadyLoggedIn", String.valueOf(miraiInstance.getBot().getId())), sender);
                        } catch (Exception e) {
                            LOGGER.error(i18n("logInFail", e.getLocalizedMessage()));
                            if (PhenylConfiguration.debug) e.printStackTrace();
                        }
                    } else sendMessage(i18n("illegalArgumentPhenyl"), sender);
                } else sendMessage(i18n("noPermission"), sender);
            }
            case "logout" -> {
                if (sender.hasPermission("phenyl.admin.logout")) {
                    if (args.length == 1) {
                        try {
                            if (miraiInstance.logOut())
                                sendMessage(i18n("logOutSuccess", miraiInstance.getBot().getNick()), sender);
                            else sendMessage(i18n("yetLoggedIn"), sender);
                        } catch (Exception e) {
                            LOGGER.warn(i18n("logOutFail", e.getLocalizedMessage()));
                            if (PhenylConfiguration.debug) e.printStackTrace();
                        }
                    } else sendMessage(i18n("illegalArgumentPhenyl"), sender);
                } else sendMessage(i18n("noPermission"), sender);
            }
            case "mute" -> {
                if (sender.hasPermission("phenyl.admin.mute")) {
                    if (args.length == 2) {
                        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);
                        if (target == null) {
                            sendMessage(i18n("playerNotFound", args[1]), sender);
                            return;
                        }
                        for (Player it : Phenyl.getMutedPlayer()) {
                            if (target.getUniqueId().toString().equals(it.uuid())) {
                                Database.updateMutedPlayer(it.uuid(), false);
                                Phenyl.getMutedPlayer().remove(it);
                                sendMessage(i18n("unMutedPlayer", args[1]), sender);
                                return;
                            }
                        }
                        Phenyl.getMutedPlayer().add(Database.getBinding(target.getUniqueId().toString()));
                        sendMessage(i18n("mutedPlayer", args[1]), sender);
                    } else sendMessage(i18n("illegalArgumentPhenyl"), sender);
                } else sendMessage(i18n("noPermission"), sender);
            }
            default -> {
                // Commands executable only for players
                if (sender instanceof ProxiedPlayer player) {
                    switch (args[0].toLowerCase()) {
                        case "bind" -> {
                            if (player.hasPermission("phenyl.use.bind")) {
                                if (args.length == 2) {
                                    String code = handleRequest(player.getName(), Long.parseLong(args[1]));
                                    sendMessage(i18n("completeBindInGroup"), player);
                                    sendMessage(PhenylConfiguration.command_prefix + PhenylConfiguration.confirm_command + " " + code, player);
                                } else sendMessage(i18n("illegalArgumentPhenyl"), player);
                            } else sendMessage(i18n("noPermission"), player);
                        }
                        case "verify" -> {
                            if (player.hasPermission("phenyl.use.verify")) {
                                if (args.length == 2) {
                                    try {
                                        sendMessage("&3" + verifier(player.getName(), args[1]), player);
                                    } catch (IllegalArgumentException e) {
                                        sendMessage("&4" + e.getMessage(), player);
                                    }
                                } else sendMessage(i18n("illegalArgumentPhenyl"), player);
                            } else sendMessage(i18n("noPermission"), player);
                        }
                        case "say" -> {
                            if (player.hasPermission("phenyl.use.say")) {
                                if (args.length == 2) {
                                    if (PhenylConfiguration.forward_mode.equalsIgnoreCase("command")) {
                                        CompletableFuture<Boolean> futureBind = CompletableFuture.supplyAsync(() -> {
                                            try {
                                                forwardToQQ(args[1], player.getName(), player.getUniqueId().toString(), getServerName(player.getServer()));
                                            } catch (NoSuchElementException e) {
                                                LOGGER.error(i18n("noSuchGroup"));
                                                if (PhenylConfiguration.debug) e.printStackTrace();
                                                return false;
                                            }
                                            return true;
                                        }).orTimeout(3, TimeUnit.SECONDS);
                                    }
                                } else sendMessage(i18n("illegalArgumentPhenyl"), player);
                            } else sendMessage(i18n("noPermission"), player);
                        }
                        case "nomessage" -> {
                            if (player.hasPermission("phenyl.use.nomessage")) {
                                if (args.length == 1) {
                                    for (Player it : Phenyl.getNoMessagePlayer()) {
                                        if (player.getUniqueId().toString().equals(it.uuid())) {
                                            Database.updateNoMessagePlayer(it.uuid(), false);
                                            Phenyl.getNoMessagePlayer().remove(it);
                                            sendMessage(i18n("receiveMessage"), player);
                                            return;
                                        }
                                    }
                                    Phenyl.getNoMessagePlayer().add(Database.getBinding(player.getUniqueId().toString()));
                                    Database.updateNoMessagePlayer(player.getUniqueId().toString(), true);
                                    sendMessage(i18n("noMessage"), player);
                                } else sendMessage(i18n("illegalArgumentPhenyl"), player);
                            } else sendMessage(i18n("noPermission"), player);
                        }
                        default -> sendMessage(i18n("commandNotFoundPhenyl"), player);
                    }
                }
            }
        }
    }
}
package live.turna.phenyl.commands;

import live.turna.phenyl.Phenyl;
import live.turna.phenyl.PhenylCommand;

import static live.turna.phenyl.message.I18n.i18n;
import static live.turna.phenyl.message.Forward.forwardToQQ;
import static live.turna.phenyl.utils.Bind.isValidQQID;
import static live.turna.phenyl.utils.Bind.verifier;
import static live.turna.phenyl.bind.BindHandler.handleRequest;
import static live.turna.phenyl.utils.Message.*;

import live.turna.phenyl.config.PhenylConfiguration;
import live.turna.phenyl.database.Database;
import live.turna.phenyl.database.Player;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
     * Execute commands.<br>
     * Command list:<br>
     * {@code help, reload, login, logout, mute} - available for both console and players to perform, requires {@code phenyl.admin.*} permission node;<br>
     * {@code bind, verify, say, nomessage} - only available for players, requires {@code phenyl.use.*} permission node.
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
        // commands executable for both CONSOLE and players.
        switch (args[0].toLowerCase()) {
            case "help" -> sendMessage(i18n("welcomeMessage", phenyl.getDescription().getVersion()) + "\n"
                    + i18n("helpMessage") + "\n"
                    + i18n("commandHelp"), sender);
            case "reload" -> {
                if (sender.hasPermission("phenyl.admin.reload")) {
                    if (args.length == 1) {
                        if (phenyl.reload())
                            sendMessage(i18n("reloadSuccessful", phenyl.getDescription().getVersion()), sender);
                    } else sendMessage(i18n("illegalArgumentPhenyl"), sender);
                } else sendMessage(i18n("noPermission"), sender);
            }
            case "login" -> {
                if (sender.hasPermission("phenyl.admin.login")) {
                    if (args.length == 1) {
                        try {
                            if (Phenyl.getMiraiInstance().logIn())
                                sendMessage(i18n("logInSuccess", Phenyl.getMiraiInstance().getBot().getNick()), sender);
                            else
                                sendMessage(i18n("alreadyLoggedIn", String.valueOf(Phenyl.getMiraiInstance().getBot().getId())), sender);
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
                            if (Phenyl.getMiraiInstance().logOut())
                                sendMessage(i18n("logOutSuccess", Phenyl.getMiraiInstance().getBot().getNick()), sender);
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
                        // check if the player is already muted
                        Player muted = getMuted(target.getUniqueId().toString());
                        if (muted.uuid() != null) {
                            Database.updateMutedPlayer(muted.uuid(), false);
                            Phenyl.getMutedPlayer().remove(muted);
                            sendMessage(i18n("unMutedPlayer", args[1]), sender);
                        } else {
                            Database.updateMutedPlayer(target.getUniqueId().toString(), true);
                            Phenyl.getMutedPlayer().add(Database.getBinding(target.getUniqueId().toString()));
                            sendMessage(i18n("mutedPlayer", args[1]), sender);
                        }
                    } else sendMessage(i18n("illegalArgumentPhenyl"), sender);
                } else sendMessage(i18n("noPermission"), sender);
            }
            default -> {
                // commands executable only for players
                if (sender instanceof ProxiedPlayer player) {
                    switch (args[0].toLowerCase()) {
                        case "bind" -> {
                            if (player.hasPermission("phenyl.use.bind")) {
                                if (args.length == 2) {
                                    if (!isValidQQID(args[1])) {
                                        sendMessage(i18n("invalidQQID"), player);
                                        return;
                                    }
                                    String code = handleRequest(player.getName(), Long.parseLong(args[1]));
                                    sendMessage(i18n("completeBindInGroup"), player);
                                    TextComponent bind = new TextComponent(PhenylConfiguration.command_prefix + PhenylConfiguration.confirm_command + " " + code);
                                    bind.setColor(ChatColor.DARK_AQUA);
                                    bind.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, PhenylConfiguration.command_prefix + PhenylConfiguration.confirm_command + " " + code));
                                    bind.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.YELLOW + i18n("clickToCopy"))));

                                    sendMessage(bind, player);
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
                                    // check if the player is already nomessaged
                                    Player noMessaged = getNoMessage(player.getUniqueId().toString());
                                    if (noMessaged.uuid() != null) {
                                        Database.updateNoMessagePlayer(noMessaged.uuid(), false);
                                        Phenyl.getNoMessagePlayer().remove(noMessaged);
                                        sendMessage(i18n("receiveMessage"), player);
                                    } else {
                                        Database.updateNoMessagePlayer(player.getUniqueId().toString(), true);
                                        Phenyl.getNoMessagePlayer().add(Database.getBinding(player.getUniqueId().toString()));
                                        sendMessage(i18n("noMessage"), player);
                                    }
                                } else sendMessage(i18n("illegalArgumentPhenyl"), player);
                            } else sendMessage(i18n("noPermission"), player);
                        }
                        default -> sendMessage(i18n("commandNotFoundPhenyl"), player);
                    }
                }
            }
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer) || args.length == 0) {
            return null;
        }
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            String argument = args[0].toLowerCase();
            completions.add("help");
            if (sender.hasPermission("phenyl.use.bind")) {
                completions.add("bind");
            }
            if (sender.hasPermission("phenyl.use.verify")) {
                completions.add("verify");
            }
            if (sender.hasPermission("phenyl.use.say")) {
                completions.add("say");
            }
            if (sender.hasPermission("phenyl.use.nomessage")) {
                completions.add("nomessage");
            }
            if (sender.hasPermission("phenyl.admin.reload")) {
                completions.add("reload");
            }
            if (sender.hasPermission("phenyl.admin.login")) {
                completions.add("login");
            }
            if (sender.hasPermission("phenyl.admin.logout")) {
                completions.add("logout");
            }
            if (sender.hasPermission("phenyl.admin.mute")) {
                completions.add("mute");
            }
            return completions.stream().filter(val -> val.startsWith(argument)).collect(Collectors.toList());
        }
        if (args.length == 2) {
            if (args[0].equals("mute")) {
                List<ProxiedPlayer> playerList = ProxyServer.getInstance().getPlayers().stream().filter(player -> player.getName().startsWith(args[1])).collect(Collectors.toList());
                playerList.forEach(player -> completions.add(player.getName()));
                playerList.clear();
                return completions;
            }
        }
        return completions;
    }
}
package live.turna.phenyl.commands;

import live.turna.phenyl.PhenylCommand;

import static live.turna.phenyl.bind.BindHandler.handleConfirm;
import static live.turna.phenyl.message.I18n.i18n;
import static live.turna.phenyl.message.Forward.forwardToQQ;
import static live.turna.phenyl.utils.Bind.isValidVerificationCode;
import static live.turna.phenyl.utils.Message.getServerName;
import static live.turna.phenyl.utils.Message.sendMessage;
import static live.turna.phenyl.bind.BindHandler.handleRequest;

import live.turna.phenyl.bind.BindResult;
import live.turna.phenyl.config.PhenylConfiguration;
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
        if (args.length == 0)
            sendMessage(i18n("welcomeVersionInfo", phenyl.getDescription().getVersion()), sender);

        // Commands executable for both CONSOLE and players.
        switch (args[0].toLowerCase()) {
            case "help" -> {
                sendMessage(i18n("welcomeMessage", phenyl.getDescription().getVersion()), sender);
                sendMessage(i18n("helpMessage"), sender);
                sendMessage(i18n("commandHelp"), sender);
            }
            case "reload" -> {
                if (sender.hasPermission("phenyl.admin.reload")) {
                    phenyl.reload();
                }
            }
            case "login" -> {
                if (sender.hasPermission("phenyl.admin.login")) {
                    try {
                        if (miraiInstance.logIn())
                            LOGGER.info(i18n("logInSuccess", miraiInstance.getBot().getNick()));
                        else
                            LOGGER.warn(i18n("alreadyLoggedIn", String.valueOf(miraiInstance.getBot().getId())));
                    } catch (Exception e) {
                        LOGGER.error(i18n("logInFail", e.getLocalizedMessage()));
                    }
                }
            }
            case "logout" -> {
                if (sender.hasPermission("phenyl.admin.logout")) {
                    try {
                        if (miraiInstance.logOut())
                            LOGGER.info(i18n("logOutSuccess", miraiInstance.getBot().getNick()));
                        else LOGGER.warn(i18n("yetLoggedIn"));
                    } catch (Exception e) {
                        LOGGER.warn(i18n("logOutFail", e.getLocalizedMessage()));
                    }
                }
            }
//            case "mute" -> {
//                if (sender.hasPermission("phenyl.admin.mute")) {
//
//                }
//            }
            default -> {
            }
        }

        // Commands executable only for players
        if (sender instanceof ProxiedPlayer player) {
            switch (args[0].toLowerCase()) {
                case "bind" -> {
                    if (player.hasPermission("phenyl.use.bind")) {
                        if (args.length == 2) {
                            String code = handleRequest(player.getName(), Long.parseLong(args[1]));
                            sendMessage(i18n("completeBindInGame"), player);
                            sendMessage(PhenylConfiguration.command_prefix + PhenylConfiguration.confirm_command + " " + code, player);
                        }
                    }
                }
                case "verify" -> {
                    if (player.hasPermission("phenyl.use.verify")) {
                        if (args.length == 2) {
                            try {
                                sendMessage(verifier(player.getName(), args[1]), player);
                            } catch (IllegalArgumentException e) {
                                sendMessage(e.toString(), player);
                            }
                        }
                    }
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
                        }
                    }
                }
//                case "nomessage" -> {
//                    if (player.hasPermission("phenyl.use.nomessage")) {
//
//                    }
//                }
                default -> {
                }

            }
        }
    }

    /**
     * @param userName The Minecraft username to request a confirmation.
     * @param code     The code to be checked.
     * @return The binding success message. Which are 1). bindSuccess when a new binding is successfully added;
     * 2). bindNoChange when the binding already exists and no operation is done; 3). changeBind when an existing binding is updated.
     * @throws IllegalArgumentException invalidCode: Verification code is neither format-correct nor valid.
     * @throws IllegalArgumentException bindFail: The request is valid, but binding attempt failed while operating database.
     */
    private static String verifier(String userName, String code) throws IllegalArgumentException {
        // Code not matching generating regex.
        if (!isValidVerificationCode(code, PhenylConfiguration.verification))
            throw new IllegalArgumentException(i18n("invalidCode"));

        BindResult bindResult = handleConfirm(userName, code);

        // Code not found in binding queue.
        if (bindResult == null) throw new IllegalArgumentException(i18n("invalidCode"));

        // Failed while updating the database.
        if (!bindResult.getSucceeded()) throw new IllegalArgumentException(i18n("bindFail"));

        //The first time someone attempts binding and succeeded.
        if (bindResult.getRegistered().mcname() == null) return i18n("bindSuccess", bindResult.getUserName());

        // Binding found in database and the request is the same as the existing, not updating.
        if (bindResult.getRegistered().mcname().equals(bindResult.getUserName()))
            return i18n("bindNoChange");

        //Update binding succeeded.
        return i18n("changeBind", bindResult.getRegistered(), bindResult.getUserName());
    }
}
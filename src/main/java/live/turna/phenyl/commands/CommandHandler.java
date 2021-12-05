package live.turna.phenyl.commands;

import live.turna.phenyl.PhenylCommand;

import static live.turna.phenyl.message.I18n.i18n;
import static live.turna.phenyl.mirai.MiraiHandler.logIn;
import static live.turna.phenyl.mirai.MiraiHandler.logOut;
import static live.turna.phenyl.utils.Message.sendMessage;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;


/**
 * CommandHandler of Phenyl.
 * @author xsling
 * @version 1.0
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
     * @param sender Command sender.
     * @param args Command arguments.
     */
    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length==0){
            sendMessage(i18n("welcomeVersionInfo", phenyl.getDescription().getVersion()), sender);
        }
        else if(sender instanceof ProxiedPlayer || sender == ProxyServer.getInstance().getConsole())
            switch (args[0].toLowerCase()){
                case "help":
                    sendMessage(i18n("welcomeMessage", phenyl.getDescription().getVersion()), sender);
                    sendMessage(i18n("helpMessage"),sender);
                    sendMessage(i18n("commandHelp"),sender);
                    break;
//                case "bind":
//                    if(sender.hasPermission("phenyl.use.bind")){
//
//                    }
//                    break;
//                case "verify":
//                    if(sender.hasPermission("phenyl.use.verify")){
//
//                    }
//                case "say":
//                    if(sender.hasPermission("phenyl.use.say")){
//
//                    }
//                    break;
//                case "nomessage":
//                    if(sender.hasPermission("phenyl.use.nomessage")){
//
//                    }
//                    break;
//                case "mute":
//                    if(sender.hasPermission("phenyl.admin.mute")){
//
//                    }
//                    break;
                case "reload":
                    if(sender.hasPermission("phenyl.admin.reload")){
                        phenyl.reload();
                    }
                    break;
                case "login":
                    if(sender.hasPermission("phenyl.admin.login")){
                        logIn();
                    }
                    break;
                case "logout":
                    if(sender.hasPermission("phenyl.admin.logout")){
                        logOut();
                    }
                    break;
        }
    }
}
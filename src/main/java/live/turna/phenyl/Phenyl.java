package live.turna.phenyl;

import live.turna.phenyl.commands.CommandHandler;
import live.turna.phenyl.config.PhenylConfiguration;
import live.turna.phenyl.database.Database;
import live.turna.phenyl.database.Player;
import live.turna.phenyl.listener.ListenerRegisterer;
import live.turna.phenyl.message.I18n;
import live.turna.phenyl.mirai.MiraiHandler;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;


public final class Phenyl extends Plugin {
    private final Logger LOGGER = LogManager.getLogger("Phenyl");

    private static Phenyl instance;
    private static I18n i18nInstance;
    private static MiraiHandler miraiInstance;
    private static List<Player> mutedPlayer = new ArrayList<>();
    private static List<Player> noMessagePlayer = new ArrayList<>();

    public static Phenyl getInstance() {
        return instance;
    }

    public static MiraiHandler getMiraiInstance() {
        return miraiInstance;
    }

    public static List<Player> getMutedPlayer() {
        return mutedPlayer;
    }

    public static List<Player> getNoMessagePlayer() {
        return noMessagePlayer;
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {
        instance = this;
        PhenylConfiguration.loadPhenylConfiguration();
        miraiInstance = new MiraiHandler(PhenylConfiguration.user_id, PhenylConfiguration.user_pass, PhenylConfiguration.login_protocol);
        miraiInstance.onEnable();
        i18nInstance = new I18n();
        i18nInstance.onEnable();
        i18nInstance.updateLocale(PhenylConfiguration.locale);
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandHandler("phenyl"));

        try {
            PhenylConfiguration.postConfiguration();
        } catch (IllegalArgumentException e) {
            LOGGER.error(e.getMessage());
            return;
        }

        miraiInstance = new MiraiHandler(PhenylConfiguration.user_id, PhenylConfiguration.user_pass, PhenylConfiguration.login_protocol);
        miraiInstance.onEnable();
        ListenerRegisterer.registerListeners();
        Database.initialize();
        mutedPlayer = Database.getMutedPlayer();
        noMessagePlayer = Database.getNoMessagePlayer();
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandHandler("phenyl"));
    }

    public boolean reload() {
        PhenylConfiguration.loadPhenylConfiguration();
        i18nInstance.updateLocale(PhenylConfiguration.locale);

        try {
            PhenylConfiguration.postConfiguration();
        } catch (IllegalArgumentException e) {
            LOGGER.error(e.getMessage());
            return false;
        }

        ListenerRegisterer.unregisterListeners();

        if (miraiInstance == null)
            miraiInstance = new MiraiHandler(PhenylConfiguration.user_id, PhenylConfiguration.user_pass, PhenylConfiguration.login_protocol);
        else
            miraiInstance.onDisable();
        miraiInstance.onEnable();

        ListenerRegisterer.registerListeners();
        Database.initialize();
        mutedPlayer = Database.getMutedPlayer();
        noMessagePlayer = Database.getNoMessagePlayer();
        return true;
    }

    @Override
    public void onDisable() {
        ListenerRegisterer.unregisterListeners();
        if (miraiInstance != null) miraiInstance.onDisable();
        i18nInstance.onDisable();
        LogManager.shutdown();
    }
}

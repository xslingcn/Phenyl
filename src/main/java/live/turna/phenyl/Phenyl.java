package live.turna.phenyl;

import live.turna.phenyl.commands.CommandHandler;
import live.turna.phenyl.config.PhenylConfiguration;
import live.turna.phenyl.database.Database;
import live.turna.phenyl.database.Player;
import live.turna.phenyl.dependency.DependencyLoader;
import live.turna.phenyl.listener.ListenerRegisterer;
import live.turna.phenyl.message.I18n;
import live.turna.phenyl.mirai.MiraiHandler;
import live.turna.phenyl.utils.Logging;
import live.turna.phenyl.utils.Metrics;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;


public final class Phenyl extends Plugin {
    private final Logger LOGGER = LogManager.getLogger("PhenylMain");

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
        Logging.onEnable();
        i18nInstance = new I18n();
        i18nInstance.onEnable();
        i18nInstance.updateLocale(PhenylConfiguration.locale);
        DependencyLoader.onEnable();
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandHandler("phenyl"));
        try {
            PhenylConfiguration.postConfiguration();
        } catch (IllegalArgumentException e) {
            LOGGER.error(e.getLocalizedMessage());
            return;
        }

        miraiInstance = new MiraiHandler(PhenylConfiguration.user_id, PhenylConfiguration.user_pass, PhenylConfiguration.login_protocol);
        miraiInstance.onEnable();
        ListenerRegisterer.registerListeners();
        Database.onEnable();
        mutedPlayer = Database.getMutedPlayer();
        noMessagePlayer = Database.getNoMessagePlayer();
        Metrics metrics = new Metrics(this, 13893);
    }

    public boolean reload() {
        mutedPlayer = new ArrayList<>();
        noMessagePlayer = new ArrayList<>();
        Database.onDisable();

        PhenylConfiguration.loadPhenylConfiguration();
        Logging.onEnable();
        i18nInstance.updateLocale(PhenylConfiguration.locale);
        ListenerRegisterer.unregisterListeners();

        try {
            PhenylConfiguration.postConfiguration();
        } catch (IllegalArgumentException e) {
            LOGGER.error(e.getLocalizedMessage());
            return false;
        }
        if (!DependencyLoader.onEnable()) return false;
        if (miraiInstance == null)
            miraiInstance = new MiraiHandler(PhenylConfiguration.user_id, PhenylConfiguration.user_pass, PhenylConfiguration.login_protocol);
        else
            miraiInstance.onDisable();
        miraiInstance.onEnable();

        ListenerRegisterer.registerListeners();
        Database.onEnable();
        mutedPlayer = Database.getMutedPlayer();
        noMessagePlayer = Database.getNoMessagePlayer();
        return true;
    }

    @Override
    public void onDisable() {
        mutedPlayer = null;
        noMessagePlayer = null;
        Database.onDisable();
        ListenerRegisterer.unregisterListeners();
        if (miraiInstance != null) miraiInstance.onDisable();
        i18nInstance.onDisable();
        LogManager.shutdown();
    }
}

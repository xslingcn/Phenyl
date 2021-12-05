package live.turna.phenyl;

import live.turna.phenyl.commands.CommandHandler;
import live.turna.phenyl.config.PhenylConfiguration;
import live.turna.phenyl.listener.ListenerRegisterer;
import live.turna.phenyl.message.I18n;
import live.turna.phenyl.mirai.MiraiEvent;
import live.turna.phenyl.mirai.MiraiHandler;

import static live.turna.phenyl.message.I18n.i18n;

import net.mamoe.mirai.Bot;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.File;


public final class Phenyl extends Plugin {
    private final Logger LOGGER = LogManager.getLogger("Phenyl");

    private static Phenyl instance;
    private transient I18n i18nInstance;

    private transient File workingDir;

    public static Phenyl getInstance() {
        return instance;
    }

    private static Level readLevel() {
        return PhenylConfiguration.debug ? Level.DEBUG : Level.INFO;
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {
        instance = this;
        workingDir = instance.getDataFolder();
        PhenylConfiguration.loadPhenylConfiguration();
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandHandler("phenyl"));
        i18nInstance = new I18n();
        i18nInstance.onEnable();
        i18nInstance.updateLocale(PhenylConfiguration.locale);

        Configurator.setLevel(LogManager.getLogger("Phenyl").getName(), readLevel());
        LOGGER.info(i18n("configLoaded"));
        if (PhenylConfiguration.debug) LOGGER.warn(i18n("debugEnabled"));

        new MiraiHandler(PhenylConfiguration.user_id, PhenylConfiguration.user_pass, PhenylConfiguration.login_protocol);
        MiraiHandler.logIn();
        MiraiEvent.listenEvents();
        ListenerRegisterer.registerListeners();
    }

    public void reload() {
        PhenylConfiguration.loadPhenylConfiguration();
        i18nInstance.updateLocale(PhenylConfiguration.locale);
        if (PhenylConfiguration.debug) LOGGER.warn(i18n("debugEnabled"));
        MiraiEvent.removeListeners();
        if (Bot.getInstanceOrNull(PhenylConfiguration.user_id) != null) MiraiHandler.logOut();
        Configurator.setLevel(LogManager.getLogger("Phenyl").getName(), readLevel());
        new MiraiHandler(PhenylConfiguration.user_id, PhenylConfiguration.user_pass, PhenylConfiguration.login_protocol);
        MiraiHandler.logIn();
        MiraiEvent.listenEvents();
        LOGGER.info(i18n("reloadSuccessful"));
    }

    @Override
    public void onDisable() {
        if (Bot.getInstanceOrNull(PhenylConfiguration.user_id) != null) MiraiHandler.logOut();
        MiraiEvent.removeListeners();
        if (i18nInstance != null) {
            i18nInstance.onDisable();
        }
        LogManager.shutdown();
    }
}

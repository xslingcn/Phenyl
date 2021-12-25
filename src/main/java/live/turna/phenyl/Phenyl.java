package live.turna.phenyl;

import live.turna.phenyl.commands.CommandHandler;
import live.turna.phenyl.config.PhenylConfiguration;
import live.turna.phenyl.database.Database;
import live.turna.phenyl.listener.ListenerRegisterer;
import live.turna.phenyl.message.I18n;
import live.turna.phenyl.mirai.MiraiHandler;

import static live.turna.phenyl.message.I18n.i18n;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public final class Phenyl extends Plugin {
    private final Logger LOGGER = LogManager.getLogger("Phenyl");

    private static Phenyl instance;
    private static I18n i18nInstance;
    private static MiraiHandler miraiInstance;

    public static Phenyl getInstance() {
        return instance;
    }

    public static MiraiHandler getMiraiInstance() {
        return miraiInstance;
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

        PhenylConfiguration.postConfiguration();

        ListenerRegisterer.registerListeners();
        Database.initialize();
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandHandler("phenyl"));
    }

    public void reload() {
        ListenerRegisterer.unregisterListeners();
        miraiInstance.onDisable();

        PhenylConfiguration.loadPhenylConfiguration();
        i18nInstance.updateLocale(PhenylConfiguration.locale);
        miraiInstance.onEnable();
        PhenylConfiguration.postConfiguration();

        ListenerRegisterer.registerListeners();
        Database.initialize();

        LOGGER.info(i18n("reloadSuccessful"));
    }

    @Override
    public void onDisable() {
        ListenerRegisterer.unregisterListeners();
        miraiInstance.onDisable();
        i18nInstance.onDisable();
        LogManager.shutdown();
    }
}

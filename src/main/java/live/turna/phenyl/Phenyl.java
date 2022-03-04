package live.turna.phenyl;

import live.turna.phenyl.commands.CommandHandler;
import live.turna.phenyl.config.PhenylConfiguration;
import live.turna.phenyl.database.StorageFactory;
import live.turna.phenyl.database.Player;
import live.turna.phenyl.database.StorageImplementation;
import live.turna.phenyl.dependency.DependencyLoader;
import live.turna.phenyl.dependency.Log4jLoader;
import live.turna.phenyl.listener.ListenerManager;
import live.turna.phenyl.message.I18n;
import live.turna.phenyl.mirai.MiraiHandler;
import live.turna.phenyl.logger.Logging;
import live.turna.phenyl.utils.Metrics;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public final class Phenyl extends Plugin {
    private static Phenyl instance;

    private final I18n i18n;
    private final PhenylConfiguration config;
    private final ListenerManager listenerManager;

    private MiraiHandler mirai;
    private StorageImplementation storage;
    private List<Player> mutedPlayer = new ArrayList<>();
    private List<Player> noMessagePlayer = new ArrayList<>();

    public static Logger LOGGER;

    public MiraiHandler getMirai() {
        return mirai;
    }

    public I18n getI18n() {
        return i18n;
    }

    public StorageImplementation getStorage() {
        return storage;
    }

    public List<Player> getMutedPlayer() {
        return mutedPlayer;
    }

    public List<Player> getNoMessagePlayer() {
        return noMessagePlayer;
    }

    public static Phenyl getInstance() {
        return instance;
    }

    public Phenyl() {
        instance = this;
        i18n = new I18n();
        config = new PhenylConfiguration();
        listenerManager = new ListenerManager();
    }

    @Override
    public void onLoad() {
        if (!instance.getDataFolder().exists())
            if (!instance.getDataFolder().mkdir())
                instance.getLogger().severe("Failed to create data folder: " + instance.getDataFolder());
        try {
            new Log4jLoader().onLoad();
            LOGGER = LogManager.getLogger("PhenylMain");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        i18n.onEnable();
        config.loadPhenylConfiguration();
        Logging.onEnable();
        i18n.updateLocale(PhenylConfiguration.locale);
        new DependencyLoader().onEnable();
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandHandler("phenyl", "", "ph"));
        if (!config.postConfiguration()) return;

        storage = new StorageFactory().createStorage(PhenylConfiguration.storage.toLowerCase());
        mutedPlayer = storage.getMutedPlayer();
        noMessagePlayer = storage.getNoMessagePlayer();
        CompletableFuture.supplyAsync(() -> {
            try {
                mirai = new MiraiHandler(PhenylConfiguration.user_id, PhenylConfiguration.user_pass, PhenylConfiguration.login_protocol);
                mirai.onEnable();
                listenerManager.register();
                return true;
            } catch (Exception e) {
                LOGGER.error(e.getLocalizedMessage());
                if (PhenylConfiguration.debug) e.printStackTrace();
                return false;
            }
        });
        new Metrics(this, 14309);
    }

    public boolean reload() {
        mutedPlayer = new ArrayList<>();
        noMessagePlayer = new ArrayList<>();
        storage.onDisable();

        config.loadPhenylConfiguration();
        Logging.onEnable();
        i18n.updateLocale(PhenylConfiguration.locale);
        listenerManager.unregister();

        if (!config.postConfiguration()) return false;
        if (!new DependencyLoader().onEnable()) return false;
        if (mirai != null) mirai.onDisable();
        mirai = null;

        storage = new StorageFactory().createStorage(PhenylConfiguration.storage.toLowerCase());
        mutedPlayer = storage.getMutedPlayer();
        noMessagePlayer = storage.getNoMessagePlayer();
        CompletableFuture.supplyAsync(() -> {
            try {
                mirai = new MiraiHandler(PhenylConfiguration.user_id, PhenylConfiguration.user_pass, PhenylConfiguration.login_protocol);
                mirai.onEnable();
                listenerManager.register();
                return true;
            } catch (Exception e) {
                LOGGER.error(e.getLocalizedMessage());
                if (PhenylConfiguration.debug) e.printStackTrace();
                return false;
            }
        });
        return true;
    }

    @Override
    public void onDisable() {
        storage.onDisable();
        listenerManager.unregister();
        if (mirai != null) mirai.onDisable();
        i18n.onDisable();
    }
}

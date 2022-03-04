package live.turna.phenyl;

import live.turna.phenyl.commands.CommandHandler;
import live.turna.phenyl.config.PhenylConfiguration;
import live.turna.phenyl.database.StorageFactory;
import live.turna.phenyl.database.Player;
import live.turna.phenyl.database.StorageImplementation;
import live.turna.phenyl.dependency.DependencyLoader;
import live.turna.phenyl.dependency.Log4jLoader;
import live.turna.phenyl.listener.ListenerRegisterer;
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

    private I18n i18n;
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

    @Override
    public void onLoad() {
        instance = this;
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
        i18n = new I18n();
        i18n.onEnable();
        new PhenylConfiguration().loadPhenylConfiguration();
        Logging.onEnable();
        i18n.updateLocale(PhenylConfiguration.locale);
        new DependencyLoader().onEnable();
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandHandler("phenyl", "", "ph"));
        if (!new PhenylConfiguration().postConfiguration()) return;

        storage = new StorageFactory().createStorage(PhenylConfiguration.storage.toLowerCase());
        mutedPlayer = storage.getMutedPlayer();
        noMessagePlayer = storage.getNoMessagePlayer();
        CompletableFuture.supplyAsync(() -> {
            mirai = new MiraiHandler(PhenylConfiguration.user_id, PhenylConfiguration.user_pass, PhenylConfiguration.login_protocol);
            mirai.onEnable();
            new ListenerRegisterer().registerListeners();
            return true;
        });
        new Metrics(this, 14309);
    }

    public boolean reload() {
        mutedPlayer = new ArrayList<>();
        noMessagePlayer = new ArrayList<>();
        storage.onDisable();

        new PhenylConfiguration().loadPhenylConfiguration();
        Logging.onEnable();
        i18n.updateLocale(PhenylConfiguration.locale);
        new ListenerRegisterer().unregisterListeners();

        if (!new PhenylConfiguration().postConfiguration()) return false;
        if (!new DependencyLoader().onEnable()) return false;
        if (mirai != null) mirai.onDisable();
        mirai = null;

        storage = new StorageFactory().createStorage(PhenylConfiguration.storage.toLowerCase());
        mutedPlayer = storage.getMutedPlayer();
        noMessagePlayer = storage.getNoMessagePlayer();
        CompletableFuture.supplyAsync(() -> {
            mirai = new MiraiHandler(PhenylConfiguration.user_id, PhenylConfiguration.user_pass, PhenylConfiguration.login_protocol);
            mirai.onEnable();
            new ListenerRegisterer().registerListeners();
            return true;
        });
        return true;
    }

    @Override
    public void onDisable() {
        mutedPlayer = null;
        noMessagePlayer = null;
        database.onDisable();
        database = null;
        new ListenerRegisterer().unregisterListeners();
        if (mirai != null) mirai.onDisable();
        mirai = null;
        i18n.onDisable();
        i18n = null;
    }
}

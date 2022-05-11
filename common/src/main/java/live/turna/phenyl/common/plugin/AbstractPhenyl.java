package live.turna.phenyl.common.plugin;

import live.turna.phenyl.common.bind.BindHandler;
import live.turna.phenyl.common.config.Config;
import live.turna.phenyl.common.database.PhenylStorage;
import live.turna.phenyl.common.database.Player;
import live.turna.phenyl.common.database.StorageFactory;
import live.turna.phenyl.common.dependency.DependencyManager;
import live.turna.phenyl.common.logger.Logging;
import live.turna.phenyl.common.message.I18n;
import live.turna.phenyl.common.mirai.MiraiHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * <b>AbstractPhenyl</b><br>
 * Implementation of {@link PhenylPlugin}.
 *
 * @since 2022/4/7 22:49
 */
public abstract class AbstractPhenyl implements PhenylPlugin {
    protected I18n i18n;
    protected DependencyManager dependencyManager;

    protected Logging logging;
    protected MiraiHandler mirai;
    protected PhenylStorage storage;
    protected BindHandler bindHandler;
    protected List<Player> mutedPlayer = new ArrayList<>();
    protected List<Player> noMessagePlayer = new ArrayList<>();
    protected List<Player> allBoundPlayer = new ArrayList<>();

    private Logger LOGGER;

    public final void load() {
        if (!setupLog4j()) throw new RuntimeException("Failed loading libraries.");
        LOGGER = LogManager.getLogger("PhenylMain");
    }

    public final boolean enable() {
        i18n = new I18n(this);
        i18n.onEnable();

        logging = new Logging(this);
        logging.onEnable();

        loadConfig();
        i18n.updateLocale(Config.locale);

        dependencyManager = new DependencyManager(this);
        dependencyManager.onEnable();

        bindHandler = new BindHandler(this);

        registerCommand();
        if (!postConfig()) return false;

        storage = new StorageFactory(this).createStorage(Config.storage.toLowerCase());
        mutedPlayer = storage.getMutedPlayer();
        noMessagePlayer = storage.getNoMessagePlayer();
        allBoundPlayer = storage.getAllBoundPlayer();
        initMirai();
        initSenderFactory();
        initMessenger();
        initForwarder();
        initMetrics();
        return true;
    }

    public final boolean reload() {
        mutedPlayer = null;
        noMessagePlayer = null;
        allBoundPlayer = null;
        storage.shutdown();

        loadConfig();
        logging.onEnable();
        i18n.updateLocale(Config.locale);
        stopListening();

        if (!postConfig()) return false;
        if (!dependencyManager.onEnable()) return false;
        if (mirai != null) mirai.onDisable();
        mirai = null;

        storage = new StorageFactory(this).createStorage(Config.storage.toLowerCase());
        mutedPlayer = storage.getMutedPlayer();
        noMessagePlayer = storage.getNoMessagePlayer();
        allBoundPlayer = storage.getAllBoundPlayer();
        initMirai();
        return true;
    }

    public final void disable() {
        storage.shutdown();
        stopListening();
        if (mirai != null) mirai.onDisable();
        i18n.onDisable();
    }

    private void initMirai() {
        CompletableFuture.supplyAsync(() -> {
            try {
                mirai = new MiraiHandler(this, Config.user_id, Config.user_pass, Config.login_protocol);
                mirai.onEnable();
                startListening();
                return true;
            } catch (Exception e) {
                LOGGER.error(e.getLocalizedMessage());
                if (Config.debug) e.printStackTrace();
                return false;
            }
        });
    }

    public MiraiHandler getMirai() {
        return mirai;
    }

    public PhenylStorage getStorage() {
        return storage;
    }

    public Logger getLogger() {
        return LOGGER;
    }

    public List<Player> getMutedPlayer() {
        return mutedPlayer;
    }

    public List<Player> getNoMessagePlayer() {
        return noMessagePlayer;
    }

    public List<Player> getAllBoundPlayer() {
        return allBoundPlayer;
    }

    public void updateBoundPlayerList() {
        allBoundPlayer = storage.getAllBoundPlayer();
    }

    public BindHandler getBindHandler() {
        return bindHandler;
    }

    /**
     * Load log4j to classpath if not existing and assign LOGGER.
     *
     * @return Whether log4j is loaded.
     */
    protected abstract boolean setupLog4j();

    protected abstract void initSenderFactory();

    protected abstract void initForwarder();

    protected abstract void initMessenger();

    protected abstract void loadConfig();

    protected abstract boolean postConfig();

    protected abstract void registerCommand();

    protected abstract void startListening();

    protected abstract void stopListening();

    protected abstract void initMetrics();
}
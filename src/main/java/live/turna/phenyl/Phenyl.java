package live.turna.phenyl;

import live.turna.phenyl.commands.CommandHandler;
import live.turna.phenyl.config.PhenylConfiguration;
import live.turna.phenyl.database.Database;
import live.turna.phenyl.database.Player;
import live.turna.phenyl.dependency.DependencyLoader;
import live.turna.phenyl.dependency.Log4jLoader;
import live.turna.phenyl.listener.ListenerRegisterer;
import live.turna.phenyl.message.I18n;
import live.turna.phenyl.mirai.MiraiHandler;
import live.turna.phenyl.utils.Logging;
import live.turna.phenyl.utils.Metrics;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public final class Phenyl extends Plugin {
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
        instance = this;
        if (!instance.getDataFolder().exists())
            if (!instance.getDataFolder().mkdir())
                instance.getLogger().severe("Failed to create data folder: " + instance.getDataFolder());
        try {
            Log4jLoader.onLoad();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        i18nInstance = new I18n();
        i18nInstance.onEnable();
        PhenylConfiguration.loadPhenylConfiguration();
        Logging.onEnable();
        i18nInstance.updateLocale(PhenylConfiguration.locale);
        DependencyLoader.onEnable();
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandHandler("phenyl", "", "ph"));
        if (!PhenylConfiguration.postConfiguration()) return;

        Database.onEnable();
        mutedPlayer = Database.getMutedPlayer();
        noMessagePlayer = Database.getNoMessagePlayer();
        CompletableFuture<Boolean> futureMirai = CompletableFuture.supplyAsync(() -> {
            miraiInstance = new MiraiHandler(PhenylConfiguration.user_id, PhenylConfiguration.user_pass, PhenylConfiguration.login_protocol);
            miraiInstance.onEnable();
            ListenerRegisterer.registerListeners();
            return true;
        });
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

        if (!PhenylConfiguration.postConfiguration()) return false;
        if (!DependencyLoader.onEnable()) return false;
        if (miraiInstance != null) miraiInstance.onDisable();
        miraiInstance = null;

        Database.onEnable();
        mutedPlayer = Database.getMutedPlayer();
        noMessagePlayer = Database.getNoMessagePlayer();
        CompletableFuture<Boolean> futureMirai = CompletableFuture.supplyAsync(() -> {
            miraiInstance = new MiraiHandler(PhenylConfiguration.user_id, PhenylConfiguration.user_pass, PhenylConfiguration.login_protocol);
            miraiInstance.onEnable();
            ListenerRegisterer.registerListeners();
            return true;
        });
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
    }
}

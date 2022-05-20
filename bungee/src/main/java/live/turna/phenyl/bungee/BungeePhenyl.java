package live.turna.phenyl.bungee;

import live.turna.phenyl.bungee.instance.*;
import live.turna.phenyl.bungee.listener.BungeeListenerManager;
import live.turna.phenyl.bungee.listener.MiraiListenerManager;
import live.turna.phenyl.common.config.Config;
import live.turna.phenyl.common.dependency.Log4jLoader;
import live.turna.phenyl.common.instance.PSender;
import live.turna.phenyl.common.listener.AbstractMiraiListenerManager;
import live.turna.phenyl.common.plugin.AbstractPhenyl;
import live.turna.phenyl.common.utils.MessageUtils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public final class BungeePhenyl extends AbstractPhenyl {

    private final transient Plugin loader;
    private final transient MiraiListenerManager miraiListenerManager = new MiraiListenerManager();
    private final transient BungeeNativeLogger nativeLogger;
    private transient BungeeListenerManager bungeeListenerManager;
    private transient BungeeConfig bungeeConfig;
    private transient BungeeSenderFactory senderFactory;
    private transient BungeeForwarder forwarder;
    private transient BungeeMessenger messenger;

    public BungeePhenyl(Plugin loader) {
        this.loader = loader;
        nativeLogger = new BungeeNativeLogger(loader.getLogger());
    }

    public void onLoad() {
        super.load();
    }

    public void onEnable() {
        super.enable();
    }

    public void onDisable() {
        super.disable();
    }

    @Override
    public AbstractMiraiListenerManager getMiraiListenerManager() {
        return miraiListenerManager;
    }

    @Override
    protected boolean setupLog4j() {
        try {
            new Log4jLoader(this).onLoad();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void initSenderFactory() {
        senderFactory = new BungeeSenderFactory(this);
    }

    @Override
    protected void initForwarder() {
        forwarder = new BungeeForwarder(this);
    }

    @Override
    protected void initMessenger() {
        messenger = new BungeeMessenger(this);
    }

    @Override
    protected void loadConfig() {
        bungeeConfig = new BungeeConfig(this);
        bungeeConfig.load();
    }

    @Override
    protected boolean postConfig() {
        return bungeeConfig.postLoad();
    }

    @Override
    protected void registerCommand() {
        ProxyServer.getInstance().getPluginManager().registerCommand(loader, new BungeeCommand(this, "phenyl", "ph"));
    }

    @Override
    protected void startListening() {
        bungeeListenerManager = new BungeeListenerManager(this);
        bungeeListenerManager.start();
    }

    @Override
    protected void stopListening() {
        bungeeListenerManager.end();
    }

    @Override
    protected void initMetrics() {
        new Metrics(loader, 14309);
    }

    @Override
    public BungeeForwarder getForwarder() {
        return forwarder;
    }

    @Override
    public BungeeMessenger getMessenger() {
        return messenger;
    }

    @Override
    public BungeeSenderFactory getSenderFactory() {
        return senderFactory;
    }

    @Override
    public String getVersion() {
        return loader.getDescription().getVersion();
    }

    @Override
    public Collection<PSender> getPlayers() {
        Collection<PSender> players = new ArrayList<>();
        ProxyServer.getInstance().getPlayers().forEach(player -> players.add(senderFactory.wrap(player)));
        return players;
    }

    @Override
    public String getPlatform() {
        return "BUNGEE";
    }

    @Override
    public File getDir() {
        return loader.getDataFolder();
    }

    @Override
    public BungeeNativeLogger getNativeLogger() {
        return nativeLogger;
    }

    @Override
    public PSender getPlayer(String username) {
        return this.getSenderFactory().wrap(ProxyServer.getInstance().getPlayer(username));
    }

    @Override
    public PSender getPlayer(UUID uuid) {
        return this.getSenderFactory().wrap(ProxyServer.getInstance().getPlayer(uuid));
    }

    @Override
    public HashMap<String, String> getOnlineList() {
        HashMap<String, String> result = new HashMap<>();
        ProxyServer.getInstance().getServers().forEach((s, serverInfo) -> {
            StringBuilder players = new StringBuilder();
            serverInfo.getPlayers().forEach(player -> players.append(player.getName()).append(","));
            if (players.isEmpty()) return;
            result.put(new MessageUtils(this).getServerName(serverInfo.getName()), players.substring(0, players.length() - 1));
        });
        return result;
    }

    @Override
    public CompletableFuture<HashMap<String, Boolean>> getStatus() {
        MessageUtils messageUtils = new MessageUtils(this);
        HashMap<String, Boolean> serverStatus = new HashMap<>();
        CompletableFuture<HashMap<String, Boolean>> statusFuture = new CompletableFuture<>();
        Config.enabled_servers.forEach(server -> {
            if (!ProxyServer.getInstance().getServers().containsKey(server)) {
                serverStatus.put(messageUtils.getServerName(server), false);
                return;
            }
            ProxyServer.getInstance().getServers().get(server).ping((result, error) -> {
                if (error == null) {
                    serverStatus.put(messageUtils.getServerName(server), true);
                } else
                    serverStatus.put(messageUtils.getServerName(server), false);
                if (serverStatus.size() == Config.enabled_servers.size())
                    statusFuture.complete(serverStatus);
            });
        });
        return statusFuture;
    }

    @Override
    public Integer getOnlineCount() {
        AtomicInteger onlineCount = new AtomicInteger();
        onlineCount.getAndSet(0);
        ProxyServer.getInstance().getServers().forEach((s, serverInfo) -> serverInfo.getPlayers().forEach(player -> onlineCount.getAndIncrement()));
        return onlineCount.get();
    }

    @Override
    public Boolean isProxy() {
        return true;
    }

    @Override
    public <T> void registerListener(T listener) {
        ProxyServer.getInstance().getPluginManager().registerListener(loader, (Listener) listener);
    }

    @Override
    public void unregisterListeners() {
        ProxyServer.getInstance().getPluginManager().unregisterListeners(loader);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        return loader.getResourceAsStream(name);
    }

    public Plugin getLoader() {
        return loader;
    }

}

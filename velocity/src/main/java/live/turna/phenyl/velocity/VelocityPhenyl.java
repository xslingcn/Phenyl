package live.turna.phenyl.velocity;

import com.velocitypowered.api.command.CommandMeta;
import live.turna.phenyl.common.config.Config;
import live.turna.phenyl.common.dependency.Log4jLoader;
import live.turna.phenyl.common.instance.PSender;
import live.turna.phenyl.common.listener.AbstractMiraiListenerManager;
import live.turna.phenyl.common.plugin.AbstractPhenyl;
import live.turna.phenyl.common.utils.MessageUtils;
import live.turna.phenyl.velocity.instance.*;
import live.turna.phenyl.velocity.listener.MiraiListenerManager;
import live.turna.phenyl.velocity.listener.VelocityListenerManager;
import live.turna.phenyl.velocity.loader.PhenylVelocityLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * <b>VelocityPhenyl</b><br>
 * *
 *
 * @since 2022/5/9 20:49
 */
public class VelocityPhenyl extends AbstractPhenyl {
    private final transient PhenylVelocityLoader loader;
    private transient MiraiListenerManager miraiListenerManager;
    private transient VelocityListenerManager velocityListenerManager;
    private transient VelocityConfig velocityConfig;
    private transient VelocityMessenger messenger;
    private transient VelocitySenderFactory senderFactory;
    private transient VelocityForwarder forwarder;
    private transient Metrics metrics;


    public VelocityPhenyl(PhenylVelocityLoader loader) {
        this.loader = loader;
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
        senderFactory = new VelocitySenderFactory(this);
    }

    @Override
    protected void initForwarder() {
        forwarder = new VelocityForwarder(this);
    }

    @Override
    protected void initMessenger() {
        messenger = new VelocityMessenger(this);
    }

    @Override
    protected void initMiraiListenerManager() {
        miraiListenerManager = new MiraiListenerManager(this);
    }

    @Override
    protected void loadConfig() {
        velocityConfig = new VelocityConfig(this);
        velocityConfig.load();
    }

    @Override
    protected boolean postConfig() {
        return velocityConfig.postLoad();
    }

    @Override
    protected void registerCommand() {
        CommandMeta meta = loader.getProxy().getCommandManager().metaBuilder("phenyl")
                .aliases("ph")
                .build();
        loader.getProxy().getCommandManager().register(meta, new VelocityCommand(this));
    }

    @Override
    protected void startServerListener() {
        velocityListenerManager = new VelocityListenerManager(this);
        velocityListenerManager.start();
    }

    @Override
    protected void stopServerListener() {
        velocityListenerManager.end();
    }

    @Override
    protected void initMetrics() {
        metrics = loader.getMetricsFactory().make(loader, 15252);
        metrics.addCustomChart(new Metrics.SingleLineChart("bound_players", () -> allBoundPlayer.size()));
    }

    @Override
    public VelocityForwarder getForwarder() {
        return forwarder;
    }

    @Override
    public VelocityMessenger getMessenger() {
        return messenger;
    }

    @Override
    public File getDir() {
        return loader.getDir();
    }

    @Override
    public VelocityNativeLogger getNativeLogger() {
        return loader.getLogger();
    }

    @Override
    public PSender getPlayer(String username) {
        return this.getSenderFactory().wrap(loader.getProxy().getPlayer(username).orElse(null));
    }

    @Override
    public PSender getPlayer(UUID uuid) {
        return this.getSenderFactory().wrap(loader.getProxy().getPlayer(uuid).orElse(null));
    }

    @Override
    public AbstractMiraiListenerManager getMiraiListenerManager() {
        return miraiListenerManager;
    }

    @Override
    public HashMap<String, String> getOnlineList() {
        HashMap<String, String> result = new HashMap<>();
        loader.getProxy().getAllServers().forEach(s -> {
            StringBuilder players = new StringBuilder();
            s.getPlayersConnected().forEach(player -> players.append(player.getUsername()).append(","));
            if (players.isEmpty()) return;
            result.put(new MessageUtils(this).getServerName(s.getServerInfo().getName()), players.substring(0, players.length() - 1));
        });
        return result;
    }

    @Override
    public CompletableFuture<HashMap<String, Boolean>> getStatus() {
        MessageUtils messageUtils = new MessageUtils(this);
        HashMap<String, Boolean> serverStatus = new HashMap<>();
        CompletableFuture<HashMap<String, Boolean>> statusFuture = new CompletableFuture<>();
        Config.enabled_servers.forEach(server -> {
            if (loader.getProxy().getServer(server).isPresent()) {
                try {
                    loader.getProxy().getServer(server).get().ping().get();
                    serverStatus.put(messageUtils.getServerName(server), true);
                } catch (Exception e) {
                    serverStatus.put(messageUtils.getServerName(server), false);
                }
            } else
                serverStatus.put(messageUtils.getServerName(server), false);

            if (serverStatus.size() == Config.enabled_servers.size())
                statusFuture.complete(serverStatus);

        });
        return statusFuture;
    }

    @Override
    public Integer getOnlineCount() {
        return loader.getProxy().getAllPlayers().size();
    }

    @Override
    public Boolean isProxy() {
        return true;
    }

    @Override
    public <T> void registerListener(T listener) {
        loader.getProxy().getEventManager().register(loader, listener);
    }

    @Override
    public void unregisterListeners() {
        loader.getProxy().getEventManager().unregisterListeners(loader);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        return loader.getClass().getClassLoader().getResourceAsStream(name);
    }

    @Override
    public VelocitySenderFactory getSenderFactory() {
        return senderFactory;
    }

    @Override
    public String getVersion() {
        return loader.getDescription().getVersion().orElse(null);
    }

    @Override
    public Collection<PSender> getPlayers() {
        Collection<PSender> players = new ArrayList<>();
        loader.getProxy().getAllPlayers().forEach(player -> players.add(senderFactory.wrap(player)));
        return players;
    }

    @Override
    public String getPlatform() {
        return "VELOCITY";
    }

    public PhenylVelocityLoader getLoader() {
        return loader;
    }
}
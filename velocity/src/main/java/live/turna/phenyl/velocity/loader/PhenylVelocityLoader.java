package live.turna.phenyl.velocity.loader;

import com.google.inject.Inject;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginDescription;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import live.turna.phenyl.velocity.Metrics;
import live.turna.phenyl.velocity.VelocityPhenyl;
import live.turna.phenyl.velocity.instance.VelocityNativeLogger;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Path;

@Plugin(id = "phenyl", name = "Phenyl", version = "1.1.12",
        url = "https://github.com/xslingcn/Phenyl", description = "Easy-to-use Chatbridge", authors = {"xslingcn"})
public class PhenylVelocityLoader {
    private final VelocityPhenyl phenyl = new VelocityPhenyl(this);
    private final VelocityNativeLogger logger;
    private final Metrics.Factory metricsFactory;

    @Inject
    private ProxyServer proxy;

    @Inject
    @DataDirectory
    private Path path;

    @Inject
    private PluginDescription description;

    @Inject
    public PhenylVelocityLoader(Logger logger, Metrics.Factory metricsFactory) {
        this.logger = new VelocityNativeLogger(logger);
        this.metricsFactory = metricsFactory;
    }

    public ProxyServer getProxy() {
        return proxy;
    }

    public VelocityNativeLogger getLogger() {
        return logger;
    }

    public File getDir() {
        return new File(String.valueOf(path));
    }

    public PluginDescription getDescription() {
        return description;
    }

    public Metrics.Factory getMetricsFactory() {
        return metricsFactory;
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onEnable(ProxyInitializeEvent e) {
        phenyl.onLoad();
        phenyl.onEnable();
    }

    @Subscribe(order = PostOrder.LAST)
    public void onDisable(ProxyShutdownEvent e) {
        phenyl.onDisable();
    }
}
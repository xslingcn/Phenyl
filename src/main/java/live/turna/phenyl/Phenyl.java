package live.turna.phenyl;

import net.md_5.bungee.api.plugin.Plugin;

import static live.turna.phenyl.config.PhenylConfiguration.loadPhenylConfiguration;

import java.util.logging.Logger;

public final class Phenyl extends Plugin {
    static final Logger LOGGER = Logger.getLogger("Phenyl");

    private static Phenyl instance;

    public static Phenyl getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        loadPhenylConfiguration();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

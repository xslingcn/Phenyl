package live.turna.phenyl;

import net.md_5.bungee.api.plugin.Plugin;

import live.turna.phenyl.config.PhenylConfiguration

public final class Phenyl extends Plugin {
    private static Phenyl instance;

    public static Phenyl getInstance() {
        return instance;
    }

    @Override

    public void onEnable() {
        new PhenylConfiguration();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

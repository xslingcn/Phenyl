package live.turna.phenyl.bungee.loader;

import live.turna.phenyl.bungee.BungeePhenyl;
import net.md_5.bungee.api.plugin.Plugin;

public final class PhenylBungeeLoader extends Plugin {
    BungeePhenyl plugin = new BungeePhenyl(this);

    @Override
    public void onLoad(){
        plugin.onLoad();
    }

    @Override
    public void onEnable() {
        plugin.onEnable();
    }

    @Override
    public void onDisable() {
        plugin.onDisable();
    }
}

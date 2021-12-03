package live.turna.phenyl;

import live.turna.phenyl.commands.CommandHandler;
import live.turna.phenyl.config.PhenylConfiguration;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.logging.Logger;

public final class Phenyl extends Plugin {
    static final Logger LOGGER = Logger.getLogger("Phenyl");

    private static Phenyl instance;
    private transient I18n i18n;

    public static Phenyl getInstance() {
        return instance;
    }

    @Override
    public void onLoad(){

    }

    @Override
    public void onEnable() {
        instance = this;
        PhenylConfiguration.loadPhenylConfiguration();
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandHandler("phenyl"));
        i18n = new I18n();
        i18n.onEnable();
        i18n.updateLocale("en");

    }

    public void reload(){
        i18n.updateLocale(PhenylConfiguration.locale);
    }
    @Override
    public void onDisable() {
        if (i18n != null) {
            i18n.onDisable();
        }
    }
}

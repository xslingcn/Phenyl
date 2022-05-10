package live.turna.phenyl.common.listener;

import live.turna.phenyl.common.plugin.AbstractPhenyl;

/**
 * <b>AbstractServerListenerManager</b><br>
 * Manager to register listeners on server-side events.
 *
 * @since 2022/5/6 15:54
 */
public abstract class AbstractServerListenerManager<P extends AbstractPhenyl> {
    protected final transient P phenyl;

    public AbstractServerListenerManager(P plugin) {
        phenyl = plugin;
    }

    abstract public void start();
    /*
     * String classPrefix = "live.turna.phenyl." + SERVER.toLowerCase() + ".listener.mirai." + SERVER;
     * phenyl.registerListener(Class.forName(classPrefix + "OnBotOfflineEvent").getConstructor(AbstractPhenyl.class).newInstance(phenyl));
     * */

    public void end() {
        phenyl.unregisterListeners();
    }

}
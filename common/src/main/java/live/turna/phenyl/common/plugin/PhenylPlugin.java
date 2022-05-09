package live.turna.phenyl.common.plugin;

import live.turna.phenyl.common.bind.BindHandler;
import live.turna.phenyl.common.database.PhenylStorage;
import live.turna.phenyl.common.database.Player;
import live.turna.phenyl.common.instance.PSender;
import live.turna.phenyl.common.instance.SenderFactory;
import live.turna.phenyl.common.listener.AbstractMiraiListenerManager;
import live.turna.phenyl.common.message.AbstractForwarder;
import live.turna.phenyl.common.message.messenger.AbstractMessenger;
import live.turna.phenyl.common.mirai.MiraiHandler;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public interface PhenylPlugin {
    MiraiHandler getMirai();

    AbstractForwarder<? extends AbstractPhenyl> getForwarder();

    AbstractMessenger<? extends AbstractPhenyl> getMessenger();

    PhenylStorage getStorage();

    File getDir();

    Logger getLogger();

    java.util.logging.Logger getNativeLogger();

    List<Player> getMutedPlayer();

    List<Player> getNoMessagePlayer();

    PSender getPlayer(String username);

    PSender getPlayer(UUID uuid);

    AbstractMiraiListenerManager getMiraiListenerManager();

    BindHandler getBindHandler();

    HashMap<String, String> getOnlineList();

    HashMap<String, Boolean> getStatus();

    Integer getOnlineCount();

    Boolean isProxy();

    <T> void registerListener(T listener);

    void unregisterListeners();

    InputStream getResourceAsStream(String name);

    SenderFactory<?, ?> getSenderFactory();

    String getVersion();

    Collection<PSender> getPlayers();
}

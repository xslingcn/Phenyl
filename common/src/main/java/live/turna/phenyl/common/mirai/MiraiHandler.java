package live.turna.phenyl.common.mirai;

import live.turna.phenyl.common.listener.AbstractMiraiListenerManager;
import live.turna.phenyl.common.plugin.AbstractPhenyl;
import live.turna.phenyl.common.utils.MiraiUtils;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.network.LoginFailedException;
import net.mamoe.mirai.utils.BotConfiguration;
import net.mamoe.mirai.utils.LoggerAdapters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static live.turna.phenyl.common.message.I18n.i18n;

/**
 * <b>MiraiHandler</b><br>
 * Manage mirai bot.
 *
 * @since 2021/12/3 22:52
 */
public class MiraiHandler {

    private final Long userID;
    private final byte[] userPass;
    private final BotConfiguration.MiraiProtocol protocol;
    private final File workingDir;
    private final transient AbstractPhenyl phenyl;
    private final transient Logger LOGGER;
    private final transient AbstractMiraiListenerManager miraiEvent;
    private Bot bot;

    /**
     * Initialize MiraiHandler.
     *
     * @param userID   Bot's QQ id.
     * @param userPass Bot's QQ password.
     * @param protocol The protocol to use.
     */
    public MiraiHandler(AbstractPhenyl plugin, String userID, String userPass, String protocol) throws IOException, NoSuchAlgorithmException {
        phenyl = plugin;
        LOGGER = phenyl.getLogger();
        miraiEvent = phenyl.getMiraiListenerManager();
        MiraiUtils miraiUtils = new MiraiUtils(phenyl);
        this.userID = Long.parseLong(userID);
        this.workingDir = miraiUtils.checkMiraiDir(new File(phenyl.getDir(), "mirai"));
        this.protocol = miraiUtils.matchProtocol(protocol);
        this.userPass = miraiUtils.md5Digest(userPass);
    }

    /**
     * Configure the bot to login.
     */
    private void configureBot() {
        System.setProperty("mirai.no-desktop", "");
        bot = BotFactory.INSTANCE.newBot(userID, userPass, new BotConfiguration() {{
            setProtocol(protocol);
            setWorkingDir(workingDir);
            setLoginSolver(new MiraiLoginSolver());
            fileBasedDeviceInfo();
            setBotLoggerSupplier(bot -> LoggerAdapters.asMiraiLogger(LogManager.getLogger("MIRAI")));
            setNetworkLoggerSupplier(bot -> LoggerAdapters.asMiraiLogger(LogManager.getLogger("MIRAI_NETWORK")));
        }});
    }

    /**
     * Get the bot instance.
     *
     * @return The bot instance.
     */
    public Bot getBot() {
        return bot;
    }

    public void onEnable() throws RuntimeException {
        try {
            LOGGER.info(i18n("loggingIn"));
            configureBot();
            miraiEvent.start(bot);
            bot.login();
            LOGGER.info(i18n("logInSuccessNoColor", bot.getNick()));
        } catch (LoginFailedException e) {
            throw new RuntimeException(i18n("logInFail", e.getLocalizedMessage()));
        }
    }

    public void onDisable() {
        miraiEvent.end();
        String nick = bot.getNick();
        bot.close();
        if (!nick.isEmpty()) LOGGER.info(i18n("logOutSuccessNoColor", nick));
    }

    public boolean logIn() throws RuntimeException {
        if (!bot.isOnline()) {
            try {
                configureBot();
                bot.login();
                miraiEvent.start(bot);
                return true;
            } catch (LoginFailedException e) {
                throw new RuntimeException(i18n("logInFail", e.getLocalizedMessage()));
            }
        } else return false;
    }

    public boolean logOut() {
        if (!bot.isOnline()) return false;
        miraiEvent.end();
        bot.close();
        return true;
    }
}
package live.turna.phenyl.mirai;

import live.turna.phenyl.Phenyl;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.network.LoginFailedException;
import net.mamoe.mirai.utils.BotConfiguration;
import net.mamoe.mirai.utils.LoggerAdapters;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static live.turna.phenyl.Phenyl.LOGGER;
import static live.turna.phenyl.message.I18n.i18n;
import static live.turna.phenyl.utils.Mirai.*;

/**
 * <b>MiraiHandler</b><br>
 * Manage mirai bot.
 *
 * @since 2021/12/3 22:52
 */
public class MiraiHandler {

    private Bot bot;
    private final Long userID;
    private final byte[] userPass;
    private final BotConfiguration.MiraiProtocol protocol;
    private final File workingDir;

    /**
     * Initialize MiraiHandler.
     *
     * @param userID   Bot's QQ id.
     * @param userPass Bot's QQ password.
     * @param protocol The protocol to use.
     */
    public MiraiHandler(String userID, String userPass, String protocol) throws IOException, NoSuchAlgorithmException {
        this.userID = Long.parseLong(userID);
        this.workingDir = checkMiraiDir(new File(Phenyl.getInstance().getDataFolder(), "mirai"));
        this.protocol = matchProtocol(protocol);
        this.userPass = md5Digest(userPass);
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
            MiraiEvent.listenEvents(bot);
            bot.login();
            LOGGER.info(i18n("logInSuccessNoColor", bot.getNick()));
        } catch (LoginFailedException e) {
            throw new RuntimeException(i18n("logInFail", e.getLocalizedMessage()));
        }
    }

    public void onDisable() {
        MiraiEvent.removeListeners();
        String nick = bot.getNick();
        bot.close();
        if (!nick.isEmpty()) LOGGER.info(i18n("logOutSuccessNoColor", nick));
    }

    public boolean logIn() throws RuntimeException {
        if (!bot.isOnline()) {
            try {
                configureBot();
                bot.login();
                MiraiEvent.listenEvents(bot);
                return true;
            } catch (LoginFailedException e) {
                throw new RuntimeException(i18n("logInFail", e.getLocalizedMessage()));
            }
        } else return false;
    }

    public boolean logOut() {
        if (!bot.isOnline()) return false;
        MiraiEvent.removeListeners();
        bot.close();
        return true;
    }
}
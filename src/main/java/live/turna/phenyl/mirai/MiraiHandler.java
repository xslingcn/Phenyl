package live.turna.phenyl.mirai;

import live.turna.phenyl.PhenylBase;
import live.turna.phenyl.config.PhenylConfiguration;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.utils.BotConfiguration;
import net.mamoe.mirai.utils.LoggerAdapters;
import org.apache.logging.log4j.MarkerManager;

import static live.turna.phenyl.message.I18n.i18n;
import static live.turna.phenyl.utils.Mirai.*;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * <b>MiraiHandler</b><br>
 * Manage mirai bot.
 *
 * @since 2021/12/3 22:52
 */
public class MiraiHandler extends PhenylBase {

    public static Bot bot;

    private static Long userID;
    private static byte[] userPass;
    private static BotConfiguration.MiraiProtocol protocol;
    private static File workingDir;

    /**
     * Initialize MiraiHandler.
     *
     * @param user_id   Bot's QQ id.
     * @param user_pass Bot's QQ password.
     * @param pro       The protocol to use.
     */
    public MiraiHandler(Long user_id, String user_pass, String pro) {
        userID = user_id;
        try {
            workingDir = checkMiraiDir(new File(phenyl.getDataFolder(), "mirai"));
        } catch (IOException e) {
            LOGGER.error(i18n("createMiraiDirFail"));
        }
        try {
            protocol = matchProtocol(pro);
        } catch (IllegalArgumentException e) {
            LOGGER.error(i18n("matchProtocolFail"));
        }
        try {
            userPass = md5Digest(user_pass);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error(i18n("digestFail") + e.getLocalizedMessage());
        }
        configureBot();
    }

    /**
     * Configures bot to login.
     */
    private static void configureBot() {
        bot = BotFactory.INSTANCE.newBot(userID, userPass, new BotConfiguration() {{
            setProtocol(protocol);
            setWorkingDir(workingDir);
            fileBasedDeviceInfo();
            if (PhenylConfiguration.debug) {
                setBotLoggerSupplier(bot -> LoggerAdapters.asMiraiLogger(LOGGER, MarkerManager.getMarker("MIRAI")));
                setNetworkLoggerSupplier(bot -> LoggerAdapters.asMiraiLogger(LOGGER, MarkerManager.getMarker("MIRAI")));
            } else {
                noBotLog();
                noNetworkLog();
            }
        }});
    }


    /**
     * Try to log in.
     */
    public static void logIn() {
        try {
            if (Bot.getInstanceOrNull(userID) == null) {
                new MiraiHandler(PhenylConfiguration.user_id, PhenylConfiguration.user_pass, PhenylConfiguration.login_protocol);
            } else if (Bot.getInstance(userID).isOnline()) {
                LOGGER.warn(i18n("alreadyLoggedIn", String.valueOf(Bot.getInstance(userID).getId())));
                return;
            }
            Bot.getInstance(userID).login();
            LOGGER.info(i18n("logInSuccess", bot.getNick()));
        } catch (Exception e) {
            LOGGER.error(i18n("logInFail", e.getLocalizedMessage()));
        }
    }

    /**
     * Try to log out.
     */
    public static void logOut() {
        try {
            if (Bot.getInstanceOrNull(userID) == null) {
                LOGGER.warn(i18n("yetLoggedIn"));
                return;
            }
            bot.close();
            LOGGER.info(i18n("logOutSuccess", String.valueOf(Bot.getInstance(userID).getId())));
        } catch (Exception e) {
            LOGGER.warn(i18n("logOutFail", e.getLocalizedMessage()));
        }
    }

}
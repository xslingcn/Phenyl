package live.turna.phenyl.mirai;

import live.turna.phenyl.PhenylBase;
import live.turna.phenyl.config.PhenylConfiguration;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.utils.BotConfiguration;
import net.mamoe.mirai.utils.LoggerAdapters;

import static live.turna.phenyl.message.I18n.i18n;
import static live.turna.phenyl.utils.Mirai.md5Digest;
import static live.turna.phenyl.utils.Mirai.checkMiraiDir;
import static live.turna.phenyl.utils.Mirai.matchProtocol;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * Manage mirai bot.
 *
 * @author xsling
 * @version 1.0
 * @since 2021/12/3 22:52
 */
public class MiraiHandler extends PhenylBase {

    public transient static Bot bot;

    private transient static Long user_id;
    private transient static byte[] user_pass;
    private transient static BotConfiguration.MiraiProtocol protocol;
    private transient static File workingDir;

    /**
     * Initialize MiraiHandler.
     *
     * @param id   Bot's QQ id.
     * @param pass Bot's QQ password.
     * @param pro  The protocol to use.
     */
    public MiraiHandler(Long id, String pass, String pro) {
        user_id = id;
        try {
            workingDir = checkMiraiDir(new File(phenyl.getDataFolder(), "mirai"));
        } catch (IOException e) {
            LOGGER.severe(i18n("createMiraiDirFail"));
        }
        try {
            protocol = matchProtocol(pro);
        } catch (IllegalArgumentException e) {
            LOGGER.severe(i18n("matchProtocolFail"));
        }
        try {
            user_pass = md5Digest(pass);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.severe(i18n("digestFail") + e.getLocalizedMessage());
        }
        configureBot();
    }

    /**
     * Configures bot to login.
     */
    private static void configureBot() {
        bot = BotFactory.INSTANCE.newBot(user_id, user_pass, new BotConfiguration() {{
            setProtocol(protocol);
            setWorkingDir(workingDir);
            fileBasedDeviceInfo();
            if (PhenylConfiguration.debug) {
                setBotLoggerSupplier(bot -> LoggerAdapters.asMiraiLogger(LOGGER));
                setNetworkLoggerSupplier(bot -> LoggerAdapters.asMiraiLogger(LOGGER));
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
            if (Bot.getInstanceOrNull(user_id) == null) {
                new MiraiHandler(PhenylConfiguration.user_id, PhenylConfiguration.user_pass, PhenylConfiguration.login_protocol);
            } else if (Bot.getInstance(user_id).isOnline()) {
                LOGGER.warning(i18n("alreadyLoggedIn", String.valueOf(Bot.getInstance(user_id).getId())));
                return;
            }
            Bot.getInstance(user_id).login();
            LOGGER.info(i18n("logInSuccess", bot.getNick()));
        } catch (Exception e) {
            LOGGER.severe(i18n("logInFail", e.getLocalizedMessage()));
        }
    }

    /**
     * Try to log out.
     */
    public static void logOut() {
        try {
            if (Bot.getInstanceOrNull(user_id) == null){
                LOGGER.warning(i18n("yetLoggedIn"));
                return;
            }
            bot.close();
            LOGGER.info(i18n("logOutSuccess", String.valueOf(Bot.getInstance(user_id).getId())));
        } catch (Exception e) {
            LOGGER.warning(i18n("logOutFail", e.getLocalizedMessage()));
        }
    }

}
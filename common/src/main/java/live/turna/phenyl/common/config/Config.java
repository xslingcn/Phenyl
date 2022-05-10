package live.turna.phenyl.common.config;

import live.turna.phenyl.common.plugin.AbstractPhenyl;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static live.turna.phenyl.common.message.I18n.i18n;
import static live.turna.phenyl.common.utils.BindUtils.isValidQQID;

/**
 * <b>Config</b><br>
 * The configuration.
 *
 * @since 2022/4/7 23:07
 */
public abstract class Config implements ConfigLoader {
    // General configuration
    public static String locale = "en";
    public static Boolean debug = false;
    // MiraiUtils configuration
    public static String user_id = "1967859840";
    public static String user_pass = "12345654321";
    public static String login_protocol = "ANDROID_PHONE";
    public static List<Long> enabled_groups = new ArrayList<>();
    // Database configuration
    public static String storage = "sqlite";
    public static Boolean save_message = true;
    public static String host = "127.0.0.1";
    public static Integer port = 3306;
    public static String username = "root";
    public static String password = "123456";
    public static String database = "Phenyl";
    public static String table_prefix = "ph_";
    // Message configuration
    public static String forward_mode = "bind";
    public static Boolean forward_image = true;
    public static Integer get_image_timeout = 5;
    public static Boolean nomessage_with_cross_server = true;
    public static Boolean new_player_greeting = true;
    public static String cross_sever_format = "&7[%sub_server%]%username%:%message%";
    public static String qq_to_server_format = "&7[QQ]%username%:%message%";
    public static String server_to_qq_format = "%username%:%message%";
    public static String online_total_format = "Total players: %player_count%";
    public static String online_list_format = "[%sub_server%]%username%";
    public static String on_join = "%username% joined the %sub_server%";
    public static String on_leave = "%username% left the game";
    public static String on_join_broadcast = "&e%username% joined the %sub_server%";
    public static String on_leave_broadcast = "&e%username% left the game";
    // Image message configuration
    public static String crafatar_url = "https://crafatar.com";
    public static Integer avatar_size = 40;
    public static Integer username_avatar_margin = 40;
    public static Integer message_min_width = 230;
    public static Integer message_max_width = 340;
    public static Integer overall_padding = 25;
    public static Integer message_offset = 10;
    public static Integer username_offset = 15;
    public static Integer username_size = 30;
    public static Integer message_size = 30;
    public static String font = "Sarasa Mono SC";
    // Bungee configuration
    public static HashMap<String, String> server_alias = new HashMap<>();
    public static List<String> enabled_servers = new ArrayList<>();
    // Binding configuration
    public static String command_prefix = "#";
    public static String bind_command = "bind";
    public static String confirm_command = "confirm";
    public static String online_command = "online";
    public static String status_command = "status";
    public static String verification = "[0-9]{6}";
    public static Integer version = 2;
    protected final transient Logger LOGGER;
    protected final transient AbstractPhenyl phenyl;

    public Config(AbstractPhenyl plugin) {
        phenyl = plugin;
        LOGGER = plugin.getLogger();
    }

    public boolean postLoad() {
        Integer latestVersion = 2;

        if ((Config.forward_mode.equalsIgnoreCase("sync") && Config.qq_to_server_format.contains("%username%"))
                || ((!Config.forward_mode.equals("bind")) && (!Config.forward_mode.equals("sync")) && (!Config.forward_mode.equals("command")))) {
            Config.forward_mode = "invalid";
            LOGGER.error(i18n("invalidSettings", i18n("invalidForward")));
            return false;
        }
        if (!isValidQQID(Config.user_id)) {
            LOGGER.error(i18n("invalidSettings", i18n("invalidQQIDSetting")));
            return false;
        }
        for (Long group : Config.enabled_groups) {
            if (!isValidQQID(group.toString())) {
                LOGGER.error(i18n("invalidSettings", i18n("invalidGroupID")));
                return false;
            }
        }
        if ((!Config.storage.equals("sqlite")) && (!Config.storage.equals("mysql")) && (!Config.storage.equals("postgresql"))) {
            LOGGER.error(i18n("invalidSettings", i18n("invalidStorage")));
            return false;
        }
        if (!Config.version.equals(latestVersion)) LOGGER.warn(i18n("updateConfig", Config.version, latestVersion));
        if (Config.debug) LOGGER.warn(i18n("debugEnabled"));
        LOGGER.info(i18n("configLoaded"));
        return true;
    }

}
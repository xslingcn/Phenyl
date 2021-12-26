package live.turna.phenyl.config;

import live.turna.phenyl.PhenylBase;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;

import static live.turna.phenyl.message.I18n.i18n;
import static live.turna.phenyl.utils.Bind.isValidQQID;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Configures phenyl.
 *
 * @author xslingcn
 * @version 1.0
 * @since 2021/12/3 1:12
 */
public class PhenylConfiguration extends PhenylBase {

    private static Configuration config;

    /**
     * Converting a YAML object to HashMap.
     *
     * @param path The keyword to locate YAML section.
     * @return HashMap $map
     */
    private static HashMap<String, String> getMap(String path) {
        Collection<String> keys;
        HashMap<String, String> map = new HashMap<>();
        keys = config.getSection(path).getKeys();
        keys.forEach((key) ->
                map.put(key, config.getSection(path).getString(key))
        );
        return map;
    }

    // General configuration
    public static String locale = "en";
    public static Boolean debug = false;

    // Mirai configuration
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
    public static int verification = 6;

    /**
     * Load configurations.
     */
    public static void loadPhenylConfiguration() {
        if (!phenyl.getDataFolder().exists())
            if (!phenyl.getDataFolder().mkdir()) {
                LOGGER.error(i18n("createDataFolderFail", phenyl.getDataFolder().toString()));
            }
        File configFile = new File(phenyl.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try (InputStream in = phenyl.getResourceAsStream("config.yml")) {
                Files.copy(in, configFile.toPath());
            } catch (IOException e) {
                LOGGER.error(i18n("createConfigFail", e.getLocalizedMessage()));
                if (PhenylConfiguration.debug) e.printStackTrace();
            }
        }
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(phenyl.getDataFolder(), "config.yml"));
        } catch (IOException e) {
            LOGGER.error(i18n("readConfigFail", e.getLocalizedMessage()));
            if (PhenylConfiguration.debug) e.printStackTrace();
        }

        // General configuration
        locale = config.getString("locale");
        debug = config.getBoolean("debug");

        // Mirai configuration
        user_id = config.getString("user_id");
        user_pass = config.getString("user_pass");
        login_protocol = config.getString("login_protocol");
        enabled_groups = config.getLongList("enabled_groups");

        // Database configuration
        storage = config.getString("storage");
        save_message = config.getBoolean("save_message");
        host = config.getString("host");
        port = config.getInt("port");
        username = config.getString("username");
        password = config.getString("password");
        database = config.getString("database");
        table_prefix = config.getString("table_prefix");

        // Message configuration
        forward_mode = config.getString("forward_mode");
        cross_sever_format = config.getString("cross_sever_format");
        qq_to_server_format = config.getString("qq_to_server_format");
        online_total_format = config.getString("online_total_format");
        online_list_format = config.getString("online_list_format");
        server_to_qq_format = config.getString("server_to_qq_format");
        on_join = config.getString("on_join");
        on_leave = config.getString("on_leave");
        on_join_broadcast = config.getString("on_join_broadcast");
        on_leave_broadcast = config.getString("on_leave_broadcast");

        // Image message configuration
        crafatar_url = config.getString("crafatar_url");
        avatar_size = config.getInt("avatar_size");
        username_avatar_margin = config.getInt("username_avatar_margin");
        message_min_width = config.getInt("message_min_width");
        message_max_width = config.getInt("message_max_width");
        overall_padding = config.getInt("overall_padding");
        username_offset = config.getInt("username_offset");
        message_offset = config.getInt("message_offset");
        username_size = config.getInt("username_size");
        message_size = config.getInt("message_size");
        font = config.getString("font");

        // Bungee configuration
        server_alias = getMap("server_alias");
        enabled_servers = config.getStringList("enabled_servers");

        // Binding configuration
        command_prefix = config.getString("command_prefix");
        bind_command = config.getString("bind_command");
        confirm_command = config.getString("confirm_command");
        online_command = config.getString("online_command");
        verification = config.getInt("verification");
    }

    /**
     * Do validations on configuration.
     *
     * @throws IllegalArgumentException invalidForward: Forward mode not correctly set or used %username% under sync mode.
     * @throws IllegalArgumentException invalidQQIDSetting: QQ ID not valid. Checked by {@link live.turna.phenyl.utils.Bind#isValidQQID(String)}.
     * @throws IllegalArgumentException invalidGroupID: Group ID not valid. Checked by {@link live.turna.phenyl.utils.Bind#isValidQQID(String)}.
     * @throws IllegalArgumentException invalidStorage: Database type not valid.
     */
    public static void postConfiguration() throws IllegalArgumentException {
        if ((forward_mode.equalsIgnoreCase("sync") && qq_to_server_format.contains("%username%"))
                || ((!forward_mode.equals("bind")) && (!forward_mode.equals("sync")) && (!forward_mode.equals("command")))) {
            forward_mode = "invalid";
            throw new IllegalArgumentException(i18n("invalidSettings", i18n("invalidForward")));
        }

        if (!isValidQQID(user_id)) {
            throw new IllegalArgumentException(i18n("invalidSettings", i18n("invalidQQIDSetting")));
        }

        enabled_groups.forEach(group -> {
            if (!isValidQQID(group.toString()))
                throw new IllegalArgumentException(i18n("invalidSettings", i18n("invalidGroupID")));
        });

        if ((!storage.equals("sqlite")) && (!storage.equals("mysql")) && (!storage.equals("postgresql"))) {
            throw new IllegalArgumentException(i18n("invalidSettings", i18n("invalidStorage")));
        }

        Configurator.setLevel(LogManager.getLogger("Phenyl").getName(), debug ? Level.DEBUG : Level.INFO);
        if (debug) LOGGER.warn(i18n("debugEnabled"));

        LOGGER.info(i18n("configLoaded"));

    }
}

package live.turna.phenyl.config;

import live.turna.phenyl.PhenylBase;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;

import static live.turna.phenyl.message.I18n.i18n;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
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
    public static String forward_mode = "bind";
    public static String forward_prefix = "&";
    public static Boolean send_cross_server = true;

    // Mirai configuration
    public static Long user_id = 1967859840L;
    public static String user_pass = "12345654321";
    public static String login_protocol = "ANDROID_PHONE";
    public static List<Long> enabled_groups = List.of();

    // Database configuration
    public static String storage = "sqlite";
    public static String host = "127.0.0.1";
    public static Integer port = 3306;
    public static String username = "root";
    public static String password = "123456";
    public static String database = "Phenyl";
    public static String table_prefix = "ph_";

    // Message configuration
    public static String cross_sever_format = "[%sub_server]%username%:%message%";
    public static String qq_to_server_format = "[QQ]%username%:%message%";
    public static String server_to_qq_format = "%username%:%message%";
    public static String on_join = "%username% joined the game.";
    public static String on_switch = "%username% joined %sub_server%.";
    public static String on_leave = "%username% left the game.";

    // Bungee configuration
    public static HashMap<String, String> server_alias = new HashMap<>();
    public static List<String> enabled_servers = List.of();

    // Binding configuration
    public static String group_command = "#bind";
    public static String confirm_command = "#confirm";
    public static int verification = 6;

    /**
     * Load configurations.
     */
    public static void loadPhenylConfiguration() {
        if (!phenyl.getDataFolder().exists())
            if (!phenyl.getDataFolder().mkdir()) {
                LOGGER.severe(i18n("createDataFolderFail"));
            }
        File configFile = new File(phenyl.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try (InputStream in = phenyl.getResourceAsStream("config.yml")) {
                Files.copy(in, configFile.toPath());
            } catch (IOException e) {
                LOGGER.severe(i18n("createConfigFail", e.getLocalizedMessage()));
            }
        }
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(phenyl.getDataFolder(), "config.yml"));
        } catch (IOException e) {
            LOGGER.severe(i18n("readConfigFail", e.getLocalizedMessage()));
        }

        //General configuration
        locale = config.getString("locale");
        debug = config.getBoolean("debug");
        forward_mode = config.getString("message_mode");
        forward_prefix = config.getString("message_prefix");
        send_cross_server = config.getBoolean("send_cross_server");

        //Mirai configuration
        user_id = config.getLong("user_id");
        user_pass = config.getString("user_pass");
        login_protocol = config.getString("login_protocol");
        enabled_groups = config.getLongList("enabled_groups");

        //Database configuration
        storage = config.getString("storage");
        host = config.getString("host");
        port = config.getInt("port");
        username = config.getString("username");
        password = config.getString("password");
        database = config.getString("database");
        table_prefix = config.getString("table_prefix");

        //Message configuration
        cross_sever_format = config.getString("cross_sever_format");
        qq_to_server_format = config.getString("qq_to_server_format");
        server_to_qq_format = config.getString("server_to_qq_format");
        on_join = config.getString("on_join");
        on_switch = config.getString("on_switch");
        on_leave = config.getString("on_leave");

        //Bungee configuration
        server_alias = getMap("server_alias");
        enabled_servers = config.getStringList("enabled_servers");

        //Binding configuration
        group_command = config.getString("group_command");
        confirm_command = config.getString("confirm_command");
        verification = config.getInt("verification");

    }
}

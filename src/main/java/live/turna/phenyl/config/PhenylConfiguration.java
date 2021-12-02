package live.turna.phenyl.config;

import live.turna.phenyl.Phenyl;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class PhenylConfiguration {
    protected static final Logger LOGGER = Logger.getLogger("Phenyl");
    private static final Phenyl phenyl = Phenyl.getInstance();

    private static Configuration config;

    /**
     * Mirai configuration
     */
    public static Long user_id = null;
    public static String user_pass = "";
    public static String login_protocol = "ANDROID_PHONE";
    public static List<Long> enabled_groups;

    /**
     * Database configuration
     */
    public static String storage = "sqlite";
    public static String host = "127.0.0.1";
    public static int port = 3306;
    public static String username = "root";
    public static String password = "";
    public static String database = "Phenyl";
    public static String table_prefix = "ph_";

    /**
     * Message configuration
     */
    public static String message_mode = "bind";
    public static String message_prefix="&";
    public static boolean send_cross_server = true;
    public static String cross_sever_format = "[%sub_server]%username%:%message%";
    public static String qq_to_server_format = "[QQ]%username%:%message%";
    public static String server_to_qq_format = "%username%:%message%";
    public static String on_join = "%username% joined the game.";
    public static String on_switch = "%username% joined %sub_server%.";
    public static String on_leave = "%username% left the game.";

    /**
     * Bungee configuration
     */
    public static Map<String,String> server_alias;
    public static  List<String> enabled_servers;

    /**
     * Binding configuration
     */
    public static String group_command = "#bind";
    public static String confirm_command = "#confirm";
    public static int verification = 6;



    public static void PhenylConfiguration(){
        try{
            if(!phenyl.getDataFolder().exists())
                if(!phenyl.getDataFolder().mkdir()) throw new IOException();
            File configFile = new File(phenyl.getDataFolder(),"config.yml");
            if(!configFile.exists()){
                try (InputStream in = phenyl.getResourceAsStream("config.yml")) {
                    Files.copy(in, configFile.toPath());
                } catch (IOException e) {
                    LOGGER.severe("Failed to create config file!");
                    e.printStackTrace();
                }
            }
            try {
                config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(phenyl.getDataFolder(), "config.yml"));
            }
            catch (IOException e){
                LOGGER.severe("Failed to read configurations!");
                e.printStackTrace();
            }
        }
        catch (IOException e){
            LOGGER.severe("Failed to create data folder!" );
            e.printStackTrace();
        }

        /**
         * Mirai configuration
         */
        user_id = config.getLong("user_id");
        user_pass = config.getString("user_pass_md5").exists() ? config.getString("user_pass_md5") : config.getString("user_pass");
        login_protocol = config.getString("login_protocol");
        enabled_groups = config.getLongList("enabled_groups");

        /**
         * Database configuration
         */
        storage = config.getString("storage");
        host = config.getString("host");
        port = config.getInt("port");
        username = config.getString("username");
        password = config.getString("password");
        database = config.getString("database");
        table_prefix = config.getString("table_prefix");

        /**
         * Message configuration
         */
        message_mode = config.getString("message_mode");
        message_prefix = config.getString("message_prefix");
        send_cross_server = config.getBoolean("send_cross_server");
        cross_sever_format = config.getString("cross_sever_format");
        qq_to_server_format = config.getString("qq_to_server_format");
        server_to_qq_format = config.getString("server_to_qq_format");
        on_join = config.getString("on_join");
        on_switch = config.getString("on_switch");
        on_leave = config.getString("on_leave");

        /**
         * Bungee configuration
         */
        server_alias = config.getStringMap("server_alias");
        enabled_servers = config.getStringList("enabled_servers");

        /**
         * Binding configuration
         */
        group_command = config.getString("group_command");
        confirm_command = config.getString("confirm_command");
        verification = config.getInt("verification");
    }
}

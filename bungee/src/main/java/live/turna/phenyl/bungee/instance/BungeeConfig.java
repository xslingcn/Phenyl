package live.turna.phenyl.bungee.instance;

import live.turna.phenyl.common.config.Config;
import live.turna.phenyl.common.config.ConfigLoader;
import live.turna.phenyl.common.plugin.AbstractPhenyl;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;

import static live.turna.phenyl.common.message.I18n.i18n;

public class BungeeConfig extends Config implements ConfigLoader {
    private static Configuration config;

    public BungeeConfig(AbstractPhenyl plugin) {
        super(plugin);
    }

    @Override
    public void load() {
        File configFile = new File(phenyl.getDir(), "config.yml");
        if (!configFile.exists()) {
            try (InputStream in = phenyl.getResourceAsStream("config.yml")) {
                Files.copy(in, configFile.toPath());
            } catch (IOException e) {
                LOGGER.error(i18n("createConfigFail", e.getLocalizedMessage()));
                if (debug) e.printStackTrace();
            }
        }
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(phenyl.getDir(), "config.yml"));
        } catch (IOException e) {
            LOGGER.error(i18n("readConfigFail", e.getLocalizedMessage()));
            if (debug) e.printStackTrace();
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
        forward_image = config.getBoolean("forward_image");
        get_image_timeout = config.getInt("get_image_timeout");
        nomessage_with_cross_server = config.getBoolean("nomessage_with_cross_server");
        new_player_greeting = config.getBoolean("new_player_greeting");
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
        status_command = config.getString("status_command");
        verification = config.getString("verification");

        version = config.getInt("version");
    }

    private HashMap<String, String> getMap(String path) {
        Collection<String> keys;
        HashMap<String, String> map = new HashMap<>();
        keys = config.getSection(path).getKeys();
        keys.forEach((key) ->
                map.put(key, config.getSection(path).getString(key))
        );
        return map;
    }
}
package live.turna.phenyl.velocity.instance;

import live.turna.phenyl.common.config.Config;
import live.turna.phenyl.common.config.ConfigLoader;
import live.turna.phenyl.common.plugin.AbstractPhenyl;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;

import static live.turna.phenyl.common.message.I18n.i18n;

/**
 * <b>VelocityConfig</b></br>
 *
 * @since 2022/5/21 3:44
 */
public class VelocityConfig extends Config implements ConfigLoader {
    private static ConfigurationNode config;

    public VelocityConfig(AbstractPhenyl plugin) {
        super(plugin);
    }

    @Override
    @SuppressWarnings("unchecked")
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
            config = YAMLConfigurationLoader.builder().setFile(new File(phenyl.getDir(), "config.yml")).build().load();
        } catch (IOException e) {
            LOGGER.error(i18n("readConfigFail", e.getLocalizedMessage()));
            if (debug) e.printStackTrace();
        }

        // General configuration
        locale = config.getNode("locale").getString();
        debug = config.getNode("debug").getBoolean();

        // Mirai configuration
        user_id = config.getNode("user_id").getString();
        user_pass = config.getNode("user_pass").getString();
        login_protocol = config.getNode("login_protocol").getString();
        enabled_groups = config.getNode("enabled_groups").getList(l -> Long.valueOf(String.valueOf(l)));

        // Database configuration
        storage = config.getNode("storage").getString();
        save_message = config.getNode("save_message").getBoolean();
        host = config.getNode("host").getString();
        port = config.getNode("port").getInt();
        username = config.getNode("username").getString();
        password = config.getNode("password").getString();
        database = config.getNode("database").getString();
        table_prefix = config.getNode("table_prefix").getString();

        // Message configuration
        forward_mode = config.getNode("forward_mode").getString();
        forward_image = config.getNode("forward_image").getBoolean();
        get_image_timeout = config.getNode("get_image_timeout").getInt();
        nomessage_with_cross_server = config.getNode("nomessage_with_cross_server").getBoolean();
        new_player_greeting = config.getNode("new_player_greeting").getBoolean();
        cross_sever_format = config.getNode("cross_sever_format").getString();
        qq_to_server_format = config.getNode("qq_to_server_format").getString();
        online_total_format = config.getNode("online_total_format").getString();
        online_list_format = config.getNode("online_list_format").getString();
        server_to_qq_format = config.getNode("server_to_qq_format").getString();
        on_join = config.getNode("on_join").getString();
        on_leave = config.getNode("on_leave").getString();
        on_join_broadcast = config.getNode("on_join_broadcast").getString();
        on_leave_broadcast = config.getNode("on_leave_broadcast").getString();

        // Image message configuration
        crafatar_url = config.getNode("crafatar_url").getString();
        avatar_size = config.getNode("avatar_size").getInt();
        username_avatar_margin = config.getNode("username_avatar_margin").getInt();
        message_min_width = config.getNode("message_min_width").getInt();
        message_max_width = config.getNode("message_max_width").getInt();
        overall_padding = config.getNode("overall_padding").getInt();
        username_offset = config.getNode("username_offset").getInt();
        message_offset = config.getNode("message_offset").getInt();
        username_size = config.getNode("username_size").getInt();
        message_size = config.getNode("message_size").getInt();
        font = config.getNode("font").getString();

        // Velocity configuration
        try {
            server_alias = config.getNode("server_alias").getValue(HashMap.class::cast);
        } catch (ClassCastException e) {
            LOGGER.error(i18n("invalidSettings", "server_alias - ") + e.getLocalizedMessage());
            if (debug) e.printStackTrace();
        }
        enabled_servers = config.getNode("enabled_servers").getList(Object::toString);

        // Binding configuration
        command_prefix = config.getNode("command_prefix").getString();
        bind_command = config.getNode("bind_command").getString();
        confirm_command = config.getNode("confirm_command").getString();
        online_command = config.getNode("online_command").getString();
        status_command = config.getNode("status_command").getString();
        verification = config.getNode("verification").getString();

        version = config.getNode("version").getInt();

    }
}
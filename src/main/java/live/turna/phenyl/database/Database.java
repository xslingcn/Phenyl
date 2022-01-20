package live.turna.phenyl.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import live.turna.phenyl.PhenylBase;
import live.turna.phenyl.config.PhenylConfiguration;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

import static live.turna.phenyl.message.I18n.i18n;

/**
 * <b>Database</b><br>
 * Encapsulate all database operation methods.
 *
 * @since 2021/12/6 2:43
 */
public class Database extends PhenylBase {
    private static SQLite sqlite = null;
    private static MySQL mysql = null;
    private static PostgreSQL postgres = null;

    /**
     * Initialize database connection and/or files.
     */
    public static void onEnable() {
        switch (PhenylConfiguration.storage.toLowerCase()) {
            case "sqlite" -> {
                File playerFile = new File(phenyl.getDataFolder(), "player.db");
                Connection playerConnection;
                File messageFile = PhenylConfiguration.save_message ? new File(phenyl.getDataFolder(), "message.db") : null;
                Connection messageConnection = null;
                if (!playerFile.exists()) {
                    try {
                        if (!playerFile.createNewFile())
                            LOGGER.error(i18n("databaseInitFail"));
                    } catch (Exception e) {
                        if (PhenylConfiguration.debug) e.printStackTrace();
                    }
                }
                if (messageFile != null) {
                    if (!messageFile.exists()) {
                        try {
                            if (!messageFile.createNewFile())
                                LOGGER.error(i18n("databaseInitFail"));
                        } catch (Exception e) {
                            if (PhenylConfiguration.debug) e.printStackTrace();
                        }
                    }
                }
                try {
                    Class.forName("org.sqlite.JDBC");
                    playerConnection = DriverManager.getConnection("jdbc:sqlite:" + playerFile.getPath());
                    if (messageFile != null) {
                        Class.forName("org.sqlite.JDBC");
                        messageConnection = DriverManager.getConnection("jdbc:sqlite:" + messageFile.getPath());
                    }
                    sqlite = new SQLite(playerConnection, messageConnection);
                    LOGGER.info(i18n("databaseSucceeded", "SQLite"));
                } catch (Exception e) {
                    LOGGER.error(i18n("databaseInitFail") + e.getLocalizedMessage());
                    if (PhenylConfiguration.debug) e.printStackTrace();
                }
            }
            case "mysql" -> {
                HikariConfig mysqlConf = new HikariConfig();
                HikariDataSource mysqlDataSource;
                mysqlConf.setDriverClassName("com.mysql.cj.jdbc.Driver");
                mysqlConf.setJdbcUrl("jdbc:mysql://" + PhenylConfiguration.host + ":" + PhenylConfiguration.port + "/" + PhenylConfiguration.database);
                mysqlConf.setUsername(PhenylConfiguration.username);
                mysqlConf.setPassword(PhenylConfiguration.password);
                mysqlConf.setLeakDetectionThreshold(10000);
                mysqlConf.addDataSourceProperty("cachePrepStmts", "true");
                mysqlConf.addDataSourceProperty("prepStmtCacheSize", "250");
                mysqlConf.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
                mysqlDataSource = new HikariDataSource(mysqlConf);
                mysql = new MySQL(mysqlDataSource);
                LOGGER.info(i18n("databaseSucceeded", "MySQL"));
            }
            case "postgresql" -> {
                HikariConfig postgresConf = new HikariConfig();
                HikariDataSource postgresDataSource;
                postgresConf.setDriverClassName("org.postgresql.Driver");
                postgresConf.setJdbcUrl("jdbc:postgresql://" + PhenylConfiguration.host + ":" + PhenylConfiguration.port + "/" + PhenylConfiguration.database);
                postgresConf.setUsername(PhenylConfiguration.username);
                postgresConf.setPassword(PhenylConfiguration.password);
                postgresConf.setLeakDetectionThreshold(10000);
                postgresConf.addDataSourceProperty("cachePrepStmts", "true");
                postgresConf.addDataSourceProperty("prepStmtCacheSize", "250");
                postgresConf.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
                postgresDataSource = new HikariDataSource(postgresConf);
                postgres = new PostgreSQL(postgresDataSource);
                LOGGER.info(i18n("databaseSucceeded", "PostgreSQL"));
            }
        }
    }

    public static void onDisable() {
        switch (PhenylConfiguration.storage.toLowerCase()) {
            case "sqlite" -> {
                if (sqlite != null)
                    sqlite.onDisable();
            }
            case "mysql" -> {
                if (mysql != null)
                    mysql.onDisable();
            }
            case "postgresql" -> {
                if (postgres != null)
                    postgres.onDisable();
            }
        }
    }

    /**
     * Try to register a player.
     *
     * @param uuid The player's Minecraft UUID.
     * @return Player instance;
     */
    public static boolean registerPlayer(String uuid, String mcname) {
        return switch (PhenylConfiguration.storage.toLowerCase()) {
            case "sqlite" -> sqlite.registerPlayer(uuid, mcname);
            case "mysql" -> mysql.registerPlayer(uuid, mcname);
            case "postgresql" -> postgres.registerPlayer(uuid, mcname);
            default -> false;
        };
    }

    /**
     * Get if a player is registered.
     *
     * @param uuid The player's Minecraft UUID.
     * @return True - player is registered; False - not registered.
     */
    public static boolean getRegistered(String uuid) {
        return switch (PhenylConfiguration.storage.toLowerCase()) {
            case "sqlite" -> sqlite.getRegistered(uuid);
            case "mysql" -> mysql.getRegistered(uuid);
            case "postgresql" -> postgres.getRegistered(uuid);
            default -> false;
        };
    }

    /**
     * Update the username if is null or does not match the param's.
     *
     * @param uuid     The player's Minecraft UUID.
     * @param userName The player's Minecraft username.
     * @return True - the username needs to be updated and that is done successfully. False - no need to update username or query failed.
     */
    public static boolean updateUserName(String uuid, String userName) {
        return switch (PhenylConfiguration.storage.toLowerCase()) {
            case "sqlite" -> sqlite.updateUserName(uuid, userName);
            case "mysql" -> mysql.updateUserName(uuid, userName);
            case "postgresql" -> postgres.updateUserName(uuid, userName);
            default -> false;
        };
    }

    /**
     * Update the player's muted setting.
     *
     * @param uuid   The player's Minecraft UUID.
     * @param toggle Whether is muted.
     * @return Whether the query succeeded.
     */
    public static boolean updateMutedPlayer(String uuid, Boolean toggle) {
        return switch (PhenylConfiguration.storage.toLowerCase()) {
            case "sqlite" -> sqlite.updateMutedPlayer(uuid, toggle);
            case "mysql" -> mysql.updateMutedPlayer(uuid, toggle);
            case "postgresql" -> postgres.updateMutedPlayer(uuid, toggle);
            default -> false;
        };
    }

    /**
     * Update the player's nomessage setting.
     *
     * @param uuid   The player's Minecraft UUID.
     * @param toggle Whether is muted.
     * @return Whether the query succeeded.
     */
    public static boolean updateNoMessagePlayer(String uuid, Boolean toggle) {
        return switch (PhenylConfiguration.storage.toLowerCase()) {
            case "sqlite" -> sqlite.updateNoMessagePlayer(uuid, toggle);
            case "mysql" -> mysql.updateNoMessagePlayer(uuid, toggle);
            case "postgresql" -> postgres.updateNoMessagePlayer(uuid, toggle);
            default -> false;
        };
    }

    /**
     * Get the list of muted players.
     *
     * @return An empty list if no player is muted, or a list of player instances.
     */
    public static List<Player> getMutedPlayer() {
        return switch (PhenylConfiguration.storage.toLowerCase()) {
            case "sqlite" -> sqlite.getMutedPlayer();
            case "mysql" -> mysql.getMutedPlayer();
            case "postgresql" -> postgres.getMutedPlayer();
            default -> new ArrayList<>();
        };
    }

    /**
     * Get the list of nomessaged players.
     *
     * @return An empty list if no player is nomessaged, or a list of player instances.
     */
    public static List<Player> getNoMessagePlayer() {
        return switch (PhenylConfiguration.storage.toLowerCase()) {
            case "sqlite" -> sqlite.getNoMessagePlayer();
            case "mysql" -> mysql.getNoMessagePlayer();
            case "postgresql" -> postgres.getNoMessagePlayer();
            default -> new ArrayList<>();
        };
    }

    /**
     * Add a binding.
     *
     * @param uuid The player's Minecraft UUID.
     * @param qqid The player's QQ ID.
     * @return True - both Minecraft username and QQ ID are successfully added to database. False - query failed.
     */
    public static boolean addBinding(String uuid, Long qqid) {
        return switch (PhenylConfiguration.storage.toLowerCase()) {
            case "sqlite" -> sqlite.addBinding(uuid, qqid);
            case "mysql" -> mysql.addBinding(uuid, qqid);
            case "postgresql" -> postgres.addBinding(uuid, qqid);
            default -> false;
        };
    }

    /**
     * Remove a player's QQ-UUID binding by setting the qqid column to NULL.
     *
     * @param uuid The player's Minecraft UUID.
     * @return True - The binding is successfully removed. False - query failed.
     */
    public static boolean removeBinding(String uuid) {
        return switch (PhenylConfiguration.storage.toLowerCase()) {
            case "sqlite" -> sqlite.removeBinding(uuid);
            case "mysql" -> mysql.removeBinding(uuid);
            case "postgresql" -> postgres.removeBinding(uuid);
            default -> false;
        };
    }

    /**
     * Get QQ ID by Minecraft UUID.
     *
     * @param uuid The player's Minecraft UUID.
     * @return Corresponding QQ ID if found, null if not.
     */
    public static Player getBinding(String uuid) {
        return switch (PhenylConfiguration.storage.toLowerCase()) {
            case "sqlite" -> sqlite.getBinding(uuid);
            case "mysql" -> mysql.getBinding(uuid);
            case "postgresql" -> postgres.getBinding(uuid);
            default -> null;
        };
    }

    /**
     * Get Minecraft username by QQ ID.
     *
     * @param qqid The player's QQ ID.
     * @return Corresponding UUID if found, null if not.
     */
    public static Player getBinding(Long qqid) {
        return switch (PhenylConfiguration.storage.toLowerCase()) {
            case "sqlite" -> sqlite.getBinding(qqid);
            case "mysql" -> mysql.getBinding(qqid);
            case "postgresql" -> postgres.getBinding(qqid);
            default -> null;
        };
    }

    /**
     * Get the player id by Minecraft username.
     *
     * @param userName The player's Minecraft username.
     * @return Corresponding id if found, null if not.
     */
    public static Integer getIDByUserName(String userName) {
        return switch (PhenylConfiguration.storage.toLowerCase()) {
            case "sqlite" -> sqlite.getIDByUserName(userName);
            case "mysql" -> mysql.getIDByUserName(userName);
            case "postgresql" -> postgres.getIDByUserName(userName);
            default -> null;
        };
    }

    /**
     * Add a message from QQ group.<br>
     * Phenyl will try to find the sender's binding first. If succeeded, the player's id and Minecraft UUID would be attached as well.
     * If not, only group ID and QQ ID would be added.
     *
     * @param content The message content.
     * @param groupID The group ID of which the message is from.
     * @param qqID    The sender's QQ ID.
     * @return True - the insert query was done successfully. False - query failed.
     */
    public static boolean addMessage(String content, Long groupID, Long qqID) {
        return switch (PhenylConfiguration.storage.toLowerCase()) {
            case "sqlite" -> sqlite.addMessage(content, groupID, qqID);
            case "mysql" -> mysql.addMessage(content, groupID, qqID);
            case "postgresql" -> postgres.addMessage(content, groupID, qqID);
            default -> false;
        };
    }

    /**
     * Add a message from Minecraft chat.
     * Phenyl will try to find the sender's binding first. If succeeded, the player's id and QQ ID would be attached as well.
     * If not, only the player's UUID would be added.
     *
     * @param content  message content.
     * @param fromuuid The sender's Minecraft UUID.
     * @return True - the insert query was done successfully. False - query failed.
     */
    public static boolean addMessage(String content, String fromuuid) {
        return switch (PhenylConfiguration.storage.toLowerCase()) {
            case "sqlite" -> sqlite.addMessage(content, fromuuid);
            case "mysql" -> mysql.addMessage(content, fromuuid);
            case "postgresql" -> postgres.addMessage(content, fromuuid);
            default -> false;
        };
    }
}
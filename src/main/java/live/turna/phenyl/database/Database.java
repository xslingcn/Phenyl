package live.turna.phenyl.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import live.turna.phenyl.PhenylBase;
import live.turna.phenyl.config.PhenylConfiguration;

import static live.turna.phenyl.message.I18n.i18n;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * <b>Database</b><br>
 * Encapsulate all database operation methods.
 *
 * @since 2021/12/6 2:43
 */
public class Database extends PhenylBase {

    /**
     * Initialize database connection and/or files.
     */
    public static void initialize() {
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
                        LOGGER.error(i18n("databaseInitFail") + e.getLocalizedMessage());
                    }
                }
                if (messageFile != null) {
                    if (!messageFile.exists()) {
                        try {
                            if (!messageFile.createNewFile())
                                LOGGER.error(i18n("databaseInitFail"));
                        } catch (Exception e) {
                            LOGGER.error(i18n("databaseInitFail") + e.getLocalizedMessage());
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
                    new SQLite(playerConnection, messageConnection);
                } catch (Exception e) {
                    LOGGER.error(i18n("databaseInitFail") + e.getLocalizedMessage());
                }
            }
            case "mysql" -> {
                HikariConfig mysql = new HikariConfig();
                HikariDataSource mysqlDataSource;
                mysql.setDriverClassName("com.mysql.jdbc.Driver");
                mysql.setJdbcUrl("jdbc:mysql://" + PhenylConfiguration.host + ":" + PhenylConfiguration.port + "/" + PhenylConfiguration.database);
                mysql.setUsername(PhenylConfiguration.username);
                mysql.setPassword(PhenylConfiguration.password);
                mysql.addDataSourceProperty("cachePrepStmts", "true");
                mysql.addDataSourceProperty("prepStmtCacheSize", "250");
                mysql.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
                mysqlDataSource = new HikariDataSource(mysql);
                new MySQL(mysqlDataSource);
            }
            case "postgresql" -> {
                HikariConfig postgres = new HikariConfig();
                HikariDataSource postgresDataSource;
                postgres.setDriverClassName("org.postgresql.Driver");
                postgres.setJdbcUrl("jdbc:postgresql://" + PhenylConfiguration.host + ":" + PhenylConfiguration.port + "/" + PhenylConfiguration.database);
                postgres.setUsername(PhenylConfiguration.username);
                postgres.setPassword(PhenylConfiguration.password);
                postgres.addDataSourceProperty("cachePrepStmts", "true");
                postgres.addDataSourceProperty("prepStmtCacheSize", "250");
                postgres.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
                postgresDataSource = new HikariDataSource(postgres);
                new PostgreSQL(postgresDataSource);
            }
        }
    }

    /**
     * Try to register a player.
     *
     * @param uuid The player's UUID.
     * @return Player instance;
     * @see SQLite#registerPlayer(String)
     */
    public static Player registerPlayer(String uuid) {
        return switch (PhenylConfiguration.storage.toLowerCase()) {
            case "sqlite" -> SQLite.registerPlayer(uuid);
            case "mysql" -> MySQL.registerPlayer(uuid);
            case "postgresql" -> PostgreSQL.registerPlayer(uuid);
            default -> null;
        };
    }

    /**
     * Update the username if is null or does not match the param's.
     *
     * @param id       The player's id.
     * @param userName The player's Minecraft username.
     * @return True - the username needs to be updated and that is done successfully. False - no need to update username or query failed.
     */
    public static boolean updateUserName(String id, String userName) {
        return switch (PhenylConfiguration.storage.toLowerCase()) {
            case "sqlite" -> SQLite.updateUserName(id, userName);
            case "mysql" -> MySQL.updateUserName(id, userName);
            case "postgresql" -> PostgreSQL.updateUserName(id, userName);
            default -> false;
        };
    }

    /**
     * Add a binding.
     *
     * @param uuid   The player's UUID.
     * @param mcname The player's Minecraft username.
     * @param qqid   The player's QQ ID.
     * @return True - both Minecraft username and QQ ID are successfully added to database. False - query failed.
     */
    public static boolean addBinding(String uuid, String mcname, Long qqid) {
        return switch (PhenylConfiguration.storage.toLowerCase()) {
            case "sqlite" -> SQLite.addBinding(uuid, mcname, qqid);
            case "mysql" -> MySQL.addBinding(uuid, mcname, qqid);
            case "postgresql" -> PostgreSQL.addBinding(uuid, mcname, qqid);
            default -> false;
        };
    }

    /**
     * Get QQ ID by Minecraft UUID.
     *
     * @param uuid The player's UUID.
     * @return Corresponding QQ ID if found, null if not.
     */
    public static Long getBinding(String uuid) {
        return switch (PhenylConfiguration.storage.toLowerCase()) {
            case "sqlite" -> SQLite.getBinding(uuid);
            case "mysql" -> MySQL.getBinding(uuid);
            case "postgresql" -> PostgreSQL.getBinding(uuid);
            default -> null;
        };
    }

    /**
     * Get Minecraft username by QQ ID.
     *
     * @param qqid The player's QQ ID.
     * @return Corresponding UUID if found, null if not.
     */
    public static String getBinding(Long qqid) {
        return switch (PhenylConfiguration.storage.toLowerCase()) {
            case "sqlite" -> SQLite.getBinding(qqid);
            case "mysql" -> MySQL.getBinding(qqid);
            case "postgresql" -> PostgreSQL.getBinding(qqid);
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
            case "sqlite" -> SQLite.getIDByUserName(userName);
            case "mysql" -> MySQL.getIDByUserName(userName);
            case "postgresql" -> PostgreSQL.getIDByUserName(userName);
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
            case "sqlite" -> SQLite.addMessage(content, groupID, qqID);
            case "mysql" -> MySQL.addMessage(content, groupID, qqID);
            case "postgresql" -> PostgreSQL.addMessage(content, groupID, qqID);
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
            case "sqlite" -> SQLite.addMessage(content, fromuuid);
            case "mysql" -> MySQL.addMessage(content, fromuuid);
            case "postgresql" -> PostgreSQL.addMessage(content, fromuuid);
            default -> false;
        };
    }
}
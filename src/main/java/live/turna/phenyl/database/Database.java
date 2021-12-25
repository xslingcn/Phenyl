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
 *
 * @since 2021/12/6 2:43
 */
public class Database extends PhenylBase {

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

    public static Player registerPlayer(String uuid) {
        return switch (PhenylConfiguration.storage.toLowerCase()) {
            case "sqlite" -> SQLite.registerPlayer(uuid);
            case "mysql" -> MySQL.registerPlayer(uuid);
            case "postgresql" -> PostgreSQL.registerPlayer(uuid);
            default -> null;
        };
    }

    public static boolean updateUserName(String id, String userName) {
        return switch (PhenylConfiguration.storage.toLowerCase()) {
            case "sqlite" -> SQLite.updateUserName(id, userName);
            case "mysql" -> MySQL.updateUserName(id, userName);
            case "postgresql" -> PostgreSQL.updateUserName(id, userName);
            default -> false;
        };
    }

    public static boolean addBinding(String uuid, String mcname, Long qqid) {
        return switch (PhenylConfiguration.storage.toLowerCase()) {
            case "sqlite" -> SQLite.addBinding(uuid, mcname, qqid);
            case "mysql" -> MySQL.addBinding(uuid, mcname, qqid);
            case "postgresql" -> PostgreSQL.addBinding(uuid, mcname, qqid);
            default -> false;
        };
    }

    public static Long getBinding(String uuid) {
        return switch (PhenylConfiguration.storage.toLowerCase()) {
            case "sqlite" -> SQLite.getBinding(uuid);
            case "mysql" -> MySQL.getBinding(uuid);
            case "postgresql" -> PostgreSQL.getBinding(uuid);
            default -> null;
        };
    }

    public static String getBinding(Long qqid) {
        return switch (PhenylConfiguration.storage.toLowerCase()) {
            case "sqlite" -> SQLite.getBinding(qqid);
            case "mysql" -> MySQL.getBinding(qqid);
            case "postgresql" -> PostgreSQL.getBinding(qqid);
            default -> null;
        };
    }

    public static Integer getIDByUserName(String userName) {
        return switch (PhenylConfiguration.storage.toLowerCase()) {
            case "sqlite" -> SQLite.getIDByUserName(userName);
            case "mysql" -> MySQL.getIDByUserName(userName);
            case "postgresql" -> PostgreSQL.getIDByUserName(userName);
            default -> null;
        };
    }

    public static boolean addMessage(String content, Long groupID, Long qqID) {
        return switch (PhenylConfiguration.storage.toLowerCase()) {
            case "sqlite" -> SQLite.addMessage(content, groupID, qqID);
            case "mysql" -> MySQL.addMessage(content, groupID, qqID);
            case "postgresql" -> PostgreSQL.addMessage(content, groupID, qqID);
            default -> false;
        };
    }

    public static boolean addMessage(String content, String fromuuid) {
        return switch (PhenylConfiguration.storage.toLowerCase()) {
            case "sqlite" -> SQLite.addMessage(content, fromuuid);
            case "mysql" -> MySQL.addMessage(content, fromuuid);
            case "postgresql" -> PostgreSQL.addMessage(content, fromuuid);
            default -> false;
        };
    }
}
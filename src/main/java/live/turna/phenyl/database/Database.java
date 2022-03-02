package live.turna.phenyl.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import live.turna.phenyl.Phenyl;
import live.turna.phenyl.config.PhenylConfiguration;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import static live.turna.phenyl.Phenyl.LOGGER;
import static live.turna.phenyl.message.I18n.i18n;

/**
 * <b>Database</b><br>
 * Encapsulate all database operation methods.
 *
 * @see live.turna.phenyl.database.Storage
 * @since 2021/12/6 2:43
 */
public class Database {
    private final Phenyl phenyl = Phenyl.getInstance();
    private Storage implementation;

    /**
     * Initialize database connection and/or files.
     */
    public void onEnable() {
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
                    implementation = new SQLite(playerConnection, messageConnection);
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
                implementation = new MySQL(mysqlDataSource);
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
                implementation = new PostgreSQL(postgresDataSource);
                LOGGER.info(i18n("databaseSucceeded", "PostgreSQL"));
            }
        }
    }

    public void onDisable() {
        if (implementation != null) {
            implementation.onDisable();
            implementation = null;
        }
    }

    public boolean registerPlayer(String uuid, String mcname) {
        return implementation.registerPlayer(uuid, mcname);
    }

    public boolean getRegistered(String uuid) {
        return implementation.getRegistered(uuid);
    }

    public boolean updateUserName(String uuid, String userName) {
        return implementation.updateUserName(uuid, userName);
    }

    public boolean updateMutedPlayer(String uuid, Boolean toggle) {
        return implementation.updateMutedPlayer(uuid, toggle);
    }

    public boolean updateNoMessagePlayer(String uuid, Boolean toggle) {
        return implementation.updateNoMessagePlayer(uuid, toggle);
    }

    public List<Player> getMutedPlayer() {
        return implementation.getMutedPlayer();
    }

    public List<Player> getNoMessagePlayer() {
        return implementation.getNoMessagePlayer();
    }

    public boolean addBinding(String uuid, Long qqid) {
        return implementation.addBinding(uuid, qqid);
    }

    public boolean removeBinding(String uuid) {
        return implementation.removeBinding(uuid);
    }

    public Player getBinding(String uuid) {
        return implementation.getBinding(uuid);
    }

    public Player getBinding(Long qqid) {
        return implementation.getBinding(qqid);
    }

    public Integer getIDByUserName(String userName) {
        return implementation.getIDByUserName(userName);
    }

    public boolean addMessage(String content, Long groupID, Long qqID) {
        return implementation.addMessage(content, groupID, qqID);
    }

    public boolean addMessage(String content, String fromuuid) {
        return implementation.addMessage(content, fromuuid);
    }
}
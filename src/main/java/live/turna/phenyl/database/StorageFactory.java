package live.turna.phenyl.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import live.turna.phenyl.Phenyl;
import live.turna.phenyl.config.PhenylConfiguration;
import live.turna.phenyl.database.sql.MySQL;
import live.turna.phenyl.database.sql.PostgreSQL;
import live.turna.phenyl.database.sql.SQLStorage;
import live.turna.phenyl.database.sql.SQLite;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

import static live.turna.phenyl.Phenyl.LOGGER;
import static live.turna.phenyl.message.I18n.i18n;

/**
 * <b>StorageFactory</b><br>
 * Create the specified storage instance.
 *
 * @see SQLStorage
 * @since 2021/12/6 2:43
 */
public class StorageFactory {
    private final Phenyl phenyl = Phenyl.getInstance();
    private PhenylStorage implementation;

    /**
     * Initialize storage.
     *
     * @param storageType The type of storage.
     * @return The implementation instance of storage.
     */
    public PhenylStorage createStorage(String storageType) {
        switch (storageType) {
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
            default -> implementation = null;
        }
        return implementation;
    }
}
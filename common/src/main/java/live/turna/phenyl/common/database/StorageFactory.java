package live.turna.phenyl.common.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import live.turna.phenyl.common.config.Config;
import live.turna.phenyl.common.database.sql.MySQL;
import live.turna.phenyl.common.database.sql.PostgreSQL;
import live.turna.phenyl.common.database.sql.SQLStorage;
import live.turna.phenyl.common.database.sql.SQLite;
import live.turna.phenyl.common.plugin.AbstractPhenyl;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

import static live.turna.phenyl.common.message.I18n.i18n;

/**
 * <b>StorageFactory</b><br>
 * Create the specified storage instance.
 *
 * @see SQLStorage
 * @since 2021/12/6 2:43
 */
public class StorageFactory {
    private final transient AbstractPhenyl phenyl;
    private final transient Logger LOGGER;

    public StorageFactory(AbstractPhenyl plugin){
        phenyl=plugin;
        LOGGER=phenyl.getLogger();
    }

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
                File playerFile = new File(phenyl.getDir(), "player.db");
                Connection playerConnection;
                File messageFile = Config.save_message ? new File(phenyl.getDir(), "message.db") : null;
                Connection messageConnection = null;
                if (!playerFile.exists()) {
                    try {
                        if (!playerFile.createNewFile())
                            LOGGER.error(i18n("databaseInitFail"));
                    } catch (Exception e) {
                        if (Config.debug) e.printStackTrace();
                    }
                }
                if (messageFile != null) {
                    if (!messageFile.exists()) {
                        try {
                            if (!messageFile.createNewFile())
                                LOGGER.error(i18n("databaseInitFail"));
                        } catch (Exception e) {
                            if (Config.debug) e.printStackTrace();
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
                    implementation = new SQLite(phenyl,playerConnection, messageConnection);
                    LOGGER.info(i18n("databaseSucceeded", "SQLite"));
                } catch (Exception e) {
                    LOGGER.error(i18n("databaseInitFail") + e.getLocalizedMessage());
                    if (Config.debug) e.printStackTrace();
                }
            }
            case "mysql" -> {
                HikariConfig mysqlConf = new HikariConfig();
                HikariDataSource mysqlDataSource;
                mysqlConf.setDriverClassName("com.mysql.cj.jdbc.Driver");
                mysqlConf.setJdbcUrl("jdbc:mysql://" + Config.host + ":" + Config.port + "/" + Config.database);
                mysqlConf.setUsername(Config.username);
                mysqlConf.setPassword(Config.password);
                mysqlConf.setLeakDetectionThreshold(10000);
                mysqlConf.addDataSourceProperty("cachePrepStmts", "true");
                mysqlConf.addDataSourceProperty("prepStmtCacheSize", "250");
                mysqlConf.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
                mysqlDataSource = new HikariDataSource(mysqlConf);
                implementation = new MySQL(phenyl,mysqlDataSource);
                LOGGER.info(i18n("databaseSucceeded", "MySQL"));
            }
            case "postgresql" -> {
                HikariConfig postgresConf = new HikariConfig();
                HikariDataSource postgresDataSource;
                postgresConf.setDriverClassName("org.postgresql.Driver");
                postgresConf.setJdbcUrl("jdbc:postgresql://" + Config.host + ":" + Config.port + "/" + Config.database);
                postgresConf.setUsername(Config.username);
                postgresConf.setPassword(Config.password);
                postgresConf.setLeakDetectionThreshold(10000);
                postgresConf.addDataSourceProperty("cachePrepStmts", "true");
                postgresConf.addDataSourceProperty("prepStmtCacheSize", "250");
                postgresConf.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
                postgresDataSource = new HikariDataSource(postgresConf);
                implementation = new PostgreSQL(phenyl,postgresDataSource);
                LOGGER.info(i18n("databaseSucceeded", "PostgreSQL"));
            }
            default -> implementation = null;
        }
        return implementation;
    }
}
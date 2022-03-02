package live.turna.phenyl.database;

import com.zaxxer.hikari.HikariDataSource;
import live.turna.phenyl.config.PhenylConfiguration;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static live.turna.phenyl.Phenyl.LOGGER;
import static live.turna.phenyl.message.I18n.i18n;

/**
 * <b>MySQL</b><br>
 * MySQL handler.
 *
 * @see live.turna.phenyl.database.SQLExecutor
 * @see live.turna.phenyl.database.Storage
 * @since 2021/12/6 1:53
 */
public class MySQL implements Storage {
    private HikariDataSource dataSource;

    private final String initPlayerTable = "CREATE TABLE IF NOT EXISTS %splayer (" +
            "id INTEGER PRIMARY KEY AUTO_INCREMENT NOT NULL, " +
            "uuid CHAR(36), " +
            "qqid BIGINT, " +
            "mcname TINYTEXT," +
            "UNIQUE (uuid)," +
            "UNIQUE (qqid)," +
            "muted TINYINT(1)," +
            "nomessage TINYINT(1));";
    private final String initMessagesTable = "CREATE TABLE IF NOT EXISTS %smessage (" +
            "content TEXT, " +
            "fromid BIGINT, " +
            "fromgroup BIGINT, " +
            "fromqqid BIGINT, " +
            "fromuuid CHAR(36), " +
            "senttime TIMESTAMP DEFAULT CURRENT_TIMESTAMP); ";
    private static final String selectPlayer = "SELECT * FROM %splayer WHERE %s=%s LIMIT 1;";
    private static final String selectPlayerList = "SELECT * FROM %splayer WHERE %s=%s;";
    private static final String updatePlayer = "UPDATE %splayer SET %s=%s WHERE %s=%s;";
    private static final String insertPlayer = "INSERT IGNORE INTO %splayer(%s) VALUES('%s');";
    private static final String insertMessage = "INSERT IGNORE INTO %smessage(%s) VALUES(%s);";

    MySQL(HikariDataSource ds) {
        dataSource = ds;
        initTables();
    }

    public void initTables() {
        try {
            Connection connection = dataSource.getConnection();
            connection.prepareStatement(String.format(initPlayerTable, PhenylConfiguration.table_prefix)).executeUpdate();
            if (PhenylConfiguration.save_message)
                connection.prepareStatement(String.format(initMessagesTable, PhenylConfiguration.table_prefix)).executeUpdate();
            connection.close();
        } catch (SQLException e) {
            LOGGER.error(i18n("databaseInitFail") + e.getLocalizedMessage());
            if (PhenylConfiguration.debug) e.printStackTrace();
        }
    }

    public Player getPlayer(String selectColumn, String selectValue) {
        if (!selectColumn.equalsIgnoreCase("qqid")) selectValue = String.format("'%s'", selectValue);

        ResultSet resultSet;
        Player result = new Player(null, null, null, null);
        try {
            Connection connection = dataSource.getConnection();
            resultSet = connection.prepareStatement(String.format(selectPlayer, PhenylConfiguration.table_prefix, selectColumn, selectValue)).executeQuery();
            if (resultSet.isBeforeFirst()) {
                resultSet.next();
                result = new Player(resultSet.getInt("id"),
                        resultSet.getString("uuid"),
                        resultSet.getString("qqid") == null ? null : Long.parseLong(resultSet.getString("qqid")),
                        resultSet.getString("mcname"));
            }
            connection.close();
            return result;
        } catch (SQLException e) {
            LOGGER.error(i18n("queryFail"), e.getLocalizedMessage());
            if (PhenylConfiguration.debug) e.printStackTrace();
        }
        return result;
    }

    public List<Player> getPlayerList(String selectColumn, String selectValue) {
        ResultSet resultSet;
        List<Player> result = new java.util.ArrayList<>();
        try {
            Connection connection = dataSource.getConnection();
            resultSet = connection.prepareStatement(String.format(selectPlayerList, PhenylConfiguration.table_prefix, selectColumn, selectValue)).executeQuery();
            if (resultSet.isBeforeFirst()) {
                while (resultSet.next()) {
                    result.add(new Player(resultSet.getInt("id"),
                            resultSet.getString("uuid"),
                            resultSet.getString("qqid") == null ? null : Long.parseLong(resultSet.getString("qqid")),
                            resultSet.getString("mcname")));
                }
            }
            connection.close();
            return result;
        } catch (SQLException e) {
            LOGGER.error(i18n("queryFail"), e.getLocalizedMessage());
            if (PhenylConfiguration.debug) e.printStackTrace();
        }
        return result;
    }

    public boolean updatePlayer(String setColumn, String setValue, String selectColumn, String selectValue) {
        if (!setColumn.equalsIgnoreCase("qqid")) setValue = String.format("'%s'", setValue);
        if (!selectColumn.equalsIgnoreCase("qqid")) selectValue = String.format("'%s'", selectValue);

        try {
            Connection connection = dataSource.getConnection();
            boolean result = connection.prepareStatement(String.format(updatePlayer, PhenylConfiguration.table_prefix, setColumn, setValue, selectColumn, selectValue)).executeUpdate() != 0;
            connection.close();
            return result;
        } catch (SQLException e) {
            LOGGER.error(i18n("queryFail"), e.getLocalizedMessage());
            if (PhenylConfiguration.debug) e.printStackTrace();
        }
        return false;
    }

    public boolean insertPlayer(String insertColumns, String insertValues) {
        try {
            Connection connection = dataSource.getConnection();
            boolean result = connection.prepareStatement(String.format(insertPlayer, PhenylConfiguration.table_prefix, insertColumns, insertValues)).executeUpdate() != 0;
            connection.close();
            return result;
        } catch (SQLException e) {
            LOGGER.error(i18n("queryFail"), e.getLocalizedMessage());
            if (PhenylConfiguration.debug) e.printStackTrace();
        }
        return false;
    }

    public boolean insertMessage(String insertColumns, String insertValues) {
        try {
            Connection connection = dataSource.getConnection();
            boolean result = connection.prepareStatement(String.format(insertMessage, PhenylConfiguration.table_prefix, insertColumns, insertValues)).executeUpdate() != 0;
            connection.close();
            return result;
        } catch (SQLException e) {
            LOGGER.error(i18n("queryFail"), e.getLocalizedMessage());
            if (PhenylConfiguration.debug) e.printStackTrace();
        }
        return false;
    }

    public void onDisable() {
        if (dataSource != null)
            dataSource.close();
        dataSource = null;
    }
}
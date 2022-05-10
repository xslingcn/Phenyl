package live.turna.phenyl.common.database.sql;

import com.zaxxer.hikari.HikariDataSource;
import live.turna.phenyl.common.config.Config;
import live.turna.phenyl.common.database.Player;
import live.turna.phenyl.common.plugin.AbstractPhenyl;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static live.turna.phenyl.common.message.I18n.i18n;

/**
 * <b>PostgreSQL</b><br>
 * PostgreSQL implementation.
 *
 * @see SQLQuery
 * @see AbstractSQLStorage
 * @since 2021/12/6 1:53
 */
public class PostgreSQL extends AbstractSQLStorage {
    private static final String selectPlayer = "SELECT * FROM %splayer WHERE %s=%s LIMIT 1;";
    private static final String selectPlayerList = "SELECT * FROM %splayer WHERE %s=%s;";
    private static final String updatePlayer = "UPDATE %splayer SET %s=%s WHERE %s=%s;";
    private static final String insertPlayer = "INSERT INTO %splayer(%s) VALUES('%s') ON CONFLICT DO NOTHING;";
    private static final String insertMessage = "INSERT INTO %smessage(%s) VALUES(%s) ON CONFLICT DO NOTHING;";
    private final transient Logger LOGGER;
    private final String initPlayerTable = "CREATE TABLE IF NOT EXISTS %splayer (" +
            "id SERIAL PRIMARY KEY, " +
            "uuid CHAR(36) UNIQUE, " +
            "qqid BIGINT UNIQUE, " +
            "mcname TEXT, " +
            "muted BOOLEAN," +
            "nomessage BOOLEAN);";
    private final String initMessagesTable = "CREATE TABLE IF NOT EXISTS %smessage (" +
            "content TEXT, " +
            "fromid BIGINT, " +
            "fromgroup BIGINT, " +
            "fromqqid BIGINT, " +
            "fromuuid CHAR(36), " +
            "senttime TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP); ";
    private HikariDataSource dataSource;

    public PostgreSQL(AbstractPhenyl plugin, HikariDataSource ds) {
        LOGGER = plugin.getLogger();
        dataSource = ds;
        initTables();
    }

    public void initTables() {
        try {
            Connection connection = dataSource.getConnection();
            connection.prepareStatement(String.format(initPlayerTable, Config.table_prefix)).executeUpdate();
            if (Config.save_message)
                connection.prepareStatement(String.format(initMessagesTable, Config.table_prefix)).executeUpdate();
            connection.close();
        } catch (SQLException e) {
            LOGGER.error(i18n("databaseInitFail") + e.getLocalizedMessage());
        }
    }

    public Player getPlayer(String selectColumn, String selectValue) {
        if (!selectColumn.equalsIgnoreCase("qqid")) selectValue = String.format("'%s'", selectValue);

        ResultSet resultSet;
        Player result = new Player(null, null, null, null);
        try {
            Connection connection = dataSource.getConnection();
            resultSet = connection.prepareStatement(String.format(selectPlayer, Config.table_prefix, selectColumn, selectValue)).executeQuery();
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
            if (Config.debug) e.printStackTrace();
        }
        return result;
    }

    public List<Player> getPlayerList(String selectColumn, String selectValue) {
        ResultSet resultSet;
        List<Player> result = new java.util.ArrayList<>();
        try {
            Connection connection = dataSource.getConnection();
            resultSet = connection.prepareStatement(String.format(selectPlayerList, Config.table_prefix, selectColumn, selectValue)).executeQuery();
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
            if (Config.debug) e.printStackTrace();
        }
        return result;
    }

    public boolean updatePlayer(String setColumn, String setValue, String selectColumn, String selectValue) {
        if (!setColumn.equalsIgnoreCase("qqid")) setValue = String.format("'%s'", setValue);
        if (!selectColumn.equalsIgnoreCase("qqid")) selectValue = String.format("'%s'", selectValue);

        try {
            Connection connection = dataSource.getConnection();
            boolean result = connection.prepareStatement(String.format(updatePlayer, Config.table_prefix, setColumn, setValue, selectColumn, selectValue)).executeUpdate() != 0;
            connection.close();
            return result;
        } catch (SQLException e) {
            LOGGER.error(i18n("queryFail"), e.getLocalizedMessage());
        }
        return false;
    }

    public boolean insertPlayer(String insertColumns, String insertValues) {
        try {
            Connection connection = dataSource.getConnection();
            boolean result = connection.prepareStatement(String.format(insertPlayer, Config.table_prefix, insertColumns, insertValues)).executeUpdate() != 0;
            connection.close();
            return result;
        } catch (SQLException e) {
            LOGGER.error(i18n("queryFail"), e.getLocalizedMessage());
        }
        return false;
    }

    public boolean insertMessage(String insertColumns, String insertValues) {
        try {
            Connection connection = dataSource.getConnection();
            boolean result = connection.prepareStatement(String.format(insertMessage, Config.table_prefix, insertColumns, insertValues)).executeUpdate() != 0;
            connection.close();
            return result;
        } catch (SQLException e) {
            LOGGER.error(i18n("queryFail"), e.getLocalizedMessage());
        }
        return false;
    }

    /*
     * Postgresql only accepts true/false as boolean values rather than 0/1.
     */
    public List<Player> getMutedPlayer() {
        return getPlayerList("muted", "true");
    }

    public List<Player> getNoMessagePlayer() {
        return getPlayerList("nomessage", "true");
    }

    public void shutdown() {
        if (dataSource != null)
            dataSource.close();
        dataSource = null;
    }
}
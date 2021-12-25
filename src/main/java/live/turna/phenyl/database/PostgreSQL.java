package live.turna.phenyl.database;

import com.zaxxer.hikari.HikariDataSource;
import live.turna.phenyl.PhenylBase;
import live.turna.phenyl.config.PhenylConfiguration;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static live.turna.phenyl.message.I18n.i18n;

/**
 * <b>PostgreSQL</b><br>
 * *
 *
 * @since 2021/12/6 1:53
 */
public class PostgreSQL extends PhenylBase {
    private static HikariDataSource dataSource;
    private static Connection connection;

    private final String initPlayerTable = "CREATE TABLE IF NOT EXISTS %splayer (" +
            "id SERIAL PRIMARY KEY, " +
            "uuid CHAR(32) UNIQUE, " +
            "qqid BIGINT UNIQUE, " +
            "mcname TEXT);";
    private final String initMessagesTable = "CREATE TABLE IF NOT EXISTS %smessage (" +
            "content TEXT, " +
            "fromid BIGINT, " +
            "fromgroup BIGINT, " +
            "fromqqid BIGINT, " +
            "fromuuid CHAR(32), " +
            "senttime TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP); ";
    private static final String selectPlayer = "SELECT * FROM %splayer WHERE %s=%s LIMIT 1;";
    private static final String updatePlayer = "UPDATE %splayer SET %s=%s WHERE %s=%s;";
    private static final String insertPlayer = "INSERT INTO %splayer(%s) VALUES('%s') ON CONFLICT DO NOTHING;";
    private static final String insertMessage = "INSERT INTO %smessage(%s) VALUES(%s) ON CONFLICT DO NOTHING;";

    public PostgreSQL(HikariDataSource ds) {
        dataSource = ds;
        initTables();
    }

    private void initTables() {
        try {
            connection = dataSource.getConnection();
            connection.prepareStatement(String.format(initPlayerTable, PhenylConfiguration.table_prefix)).executeUpdate();
            if (PhenylConfiguration.save_message)
                connection.prepareStatement(String.format(initMessagesTable, PhenylConfiguration.table_prefix)).executeUpdate();
            connection.close();
        } catch (SQLException e) {
            LOGGER.error(i18n("databaseInitFail") + e.getLocalizedMessage());
        }
    }

    private static Player getPlayer(String selectColumn, String selectValue) {
        if (!selectColumn.equalsIgnoreCase("qqid")) selectValue = String.format("'%s'", selectValue);

        ResultSet resultSet;
        try {
            connection = dataSource.getConnection();
            resultSet = connection.prepareStatement(String.format(selectPlayer, PhenylConfiguration.table_prefix, selectColumn, selectValue)).executeQuery();
            if (resultSet.isBeforeFirst()) {
                resultSet.next();
                Player result = new Player(resultSet.getInt("id"),
                        resultSet.getString("uuid"),
                        resultSet.getString("qqid") == null ? null : Long.parseLong(resultSet.getString("qqid")),
                        resultSet.getString("mcname"));
                resultSet.close();
                connection.close();
                return result;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Player(null, null, null, null);
    }

    private static boolean updatePlayer(String setColumn, String setValue, String selectColumn, String selectValue) {
        if (!setColumn.equalsIgnoreCase("qqid")) setValue = String.format("'%s'", setValue);
        if (!selectColumn.equalsIgnoreCase("qqid")) selectValue = String.format("'%s'", selectValue);

        try {
            connection = dataSource.getConnection();
            boolean result = connection.prepareStatement(String.format(updatePlayer, PhenylConfiguration.table_prefix, setColumn, setValue, selectColumn, selectValue)).executeUpdate() != 0;
            connection.close();
            return result;
        } catch (SQLException e) {
            LOGGER.error(i18n("queryFail"), e.getLocalizedMessage());
        }
        return false;
    }

    private static boolean insertPlayer(String insertColumns, String insertValues) {
        try {
            connection = dataSource.getConnection();
            boolean result = connection.prepareStatement(String.format(insertPlayer, PhenylConfiguration.table_prefix, insertColumns, insertValues)).executeUpdate() != 0;
            connection.close();
            return result;
        } catch (SQLException e) {
            LOGGER.error(i18n("queryFail"), e.getLocalizedMessage());
        }
        return false;
    }

    private static boolean insertMessage(String insertColumns, String insertValues) {
        try {
            connection = dataSource.getConnection();
            boolean result = connection.prepareStatement(String.format(insertMessage, PhenylConfiguration.table_prefix, insertColumns, insertValues)).executeUpdate() != 0;
            connection.close();
            return result;
        } catch (SQLException e) {
            LOGGER.error(i18n("queryFail"), e.getLocalizedMessage());
        }
        return false;
    }

    public static Player registerPlayer(String uuid) {
        Player result = getPlayer("uuid", uuid);
        if (result.uuid() == null) {
            insertPlayer("uuid", uuid);
            result = getPlayer("uuid", uuid);
        }
        return result;
    }

    public static boolean updateUserName(String id, String userName) {
        Player result = getPlayer("id", id);
        if (result.mcname() == null || !result.mcname().equals(userName)) {
            return updatePlayer("mcname", userName, "id", id);
        }
        return false;
    }

    public static boolean addBinding(String uuid, String mcname, Long qqid) {
        return updatePlayer("mcname", mcname, "uuid", uuid) &&
                updatePlayer("qqid", qqid.toString(), "uuid", uuid);
    }

    public static Long getBinding(String uuid) {
        return getPlayer("uuid", uuid).qqid();
    }

    public static String getBinding(Long qqid) {
        return getPlayer("qqid", qqid.toString()).mcname();
    }

    public static Integer getIDByUserName(String userName) {
        return getPlayer("mcname", userName).id();
    }

    public static boolean addMessage(String content, Long groupID, Long qqID) {
        Player result = getPlayer("qqid", qqID.toString());
        if (result.uuid() != null)
            return insertMessage("content,fromid,fromuuid,fromgroup,fromqqid", String.format("'%s',%s,'%s',%s,%s", content, result.id(), result.uuid(), groupID.toString(), qqID));
        else
            return insertMessage("content,fromgroup,fromqqid", String.format("'%s',%s,%s", content, groupID.toString(), qqID));
    }

    public static boolean addMessage(String content, String fromuuid) {
        Player result = getPlayer("uuid", fromuuid);
        if (result.qqid() != null)
            return insertMessage("content,fromid,fromqqid", String.format("'%s',%s,%s", content, result.id(), result.qqid()));
        else if (result.uuid() == null) registerPlayer(fromuuid);
        return insertMessage("content,fromuuid", String.format("'%s','%s'", content, fromuuid));
    }
}
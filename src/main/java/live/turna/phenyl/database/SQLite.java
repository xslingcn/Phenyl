package live.turna.phenyl.database;

import live.turna.phenyl.PhenylBase;
import live.turna.phenyl.config.PhenylConfiguration;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static live.turna.phenyl.message.I18n.i18n;

/**
 * <b>SQLite</b><br>
 * *
 *
 * @since 2021/12/5 21:08
 */
public class SQLite extends PhenylBase {
    private static Connection playerConnection;
    private static Connection messageConnection;
    private static Statement player;
    private static Statement message;

    private final String initPlayerTable = "CREATE TABLE IF NOT EXISTS player (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL, " +
            "uuid CHAR(32), " +
            "qqid BIGINT, " +
            "mcname TINYTEXT);";
    private final String initMessagesTable = "CREATE TABLE IF NOT EXISTS message (" +
            "content TINYTEXT, " +
            "fromid BIGINT, " +
            "fromgroup BIGINT, " +
            "fromqqid BIGINT, " +
            "fromuuid CHAR(32), " +
            "senttime TIMESTAMP DEFAULT CURRENT_TIMESTAMP); ";
    private static final String selectPlayer = "SELECT * FROM player WHERE %s=%s LIMIT 1;";
    private static final String updatePlayer = "UPDATE player SET %s=%s WHERE %s=%s;";
    private static final String insertPlayer = "INSERT OR IGNORE INTO player(%s) VALUES('%s');";
    private static final String insertMessage = "INSERT INTO message(%s) VALUES(%s);";

    public SQLite(Connection playerC, @Nullable Connection messageC) {
        playerConnection = playerC;
        messageConnection = messageC;
        initTables();
    }

    private void initTables() {
        try {
            player = playerConnection.createStatement();
            player.execute(initPlayerTable);
            player.close();
            if (messageConnection != null) {
                message = messageConnection.createStatement();
                message.execute(initMessagesTable);
                message.close();
            } else message = null;
        } catch (SQLException e) {
            LOGGER.error(i18n("databaseInitFail") + e.getLocalizedMessage());
            if (PhenylConfiguration.debug) e.printStackTrace();
        }
    }

    private static Player getPlayer(String selectColumn, String selectValue) {
        if (!selectColumn.equalsIgnoreCase("qqid")) selectValue = String.format("'%s'", selectValue);

        ResultSet resultSet;
        try {
            player = playerConnection.createStatement();
            resultSet = player.executeQuery(String.format(selectPlayer, selectColumn, selectValue));
            if (resultSet.isBeforeFirst()) {
                resultSet.next();
                Player result = new Player(resultSet.getInt("id"),
                        resultSet.getString("uuid"),
                        resultSet.getString("qqid") == null ? null : Long.parseLong(resultSet.getString("qqid")),
                        resultSet.getString("mcname"));
                resultSet.close();
                player.close();
                return result;
            }
        } catch (SQLException e) {
            LOGGER.error(i18n("queryFail"), e.getLocalizedMessage());
            if (PhenylConfiguration.debug) e.printStackTrace();
        }
        return new Player(null, null, null, null);
    }

    private static boolean updatePlayer(String setColumn, String setValue, String selectColumn, String selectValue) {
        if (!setColumn.equalsIgnoreCase("qqid")) setValue = String.format("'%s'", setValue);
        if (!selectColumn.equalsIgnoreCase("qqid")) selectValue = String.format("'%s'", selectValue);

        try {
            player = playerConnection.createStatement();
            boolean result = player.executeUpdate(String.format(updatePlayer, setColumn, setValue, selectColumn, selectValue)) != 0;
            player.close();
            return result;
        } catch (SQLException e) {
            LOGGER.error(i18n("queryFail"), e.getLocalizedMessage());
            if (PhenylConfiguration.debug) e.printStackTrace();
        }
        return false;
    }

    private static boolean insertPlayer(String insertColumns, String insertValues) {
        try {
            player = playerConnection.createStatement();
            boolean result = (player.executeUpdate(String.format(insertPlayer, insertColumns, insertValues)) != 0);
            player.close();
            return result;
        } catch (SQLException e) {
            LOGGER.error(i18n("queryFail"), e.getLocalizedMessage());
            if (PhenylConfiguration.debug) e.printStackTrace();
        }
        return false;
    }

    private static boolean insertMessage(String insertColumns, String insertValues) {
        try {
            message = messageConnection.createStatement();
            boolean result = message.executeUpdate(String.format(insertMessage, insertColumns, insertValues)) != 0;
            message.close();
            return result;
        } catch (SQLException e) {
            LOGGER.error(i18n("queryFail"), e.getLocalizedMessage());
            if (PhenylConfiguration.debug) e.printStackTrace();
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

    // qq[0] GroupID; qq[1] QQID
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
            return insertMessage("content,fromid,fromqqid,fromuuid", String.format("'%s',%s,%s,'%s'", content, result.id(), result.qqid(), result.uuid()));
        else if (result.uuid() == null) registerPlayer(fromuuid);
        return insertMessage("content,fromuuid", String.format("'%s','%s'", content, fromuuid));
    }
}
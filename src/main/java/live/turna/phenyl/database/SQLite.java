package live.turna.phenyl.database;

import com.sun.tools.jconsole.JConsoleContext;
import live.turna.phenyl.PhenylBase;
import live.turna.phenyl.config.PhenylConfiguration;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static live.turna.phenyl.message.I18n.i18n;

/**
 * <b>SQLite</b><br>
 * SQLite handler.
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
            "mcname TINYTEXT," +
            "muted TINYINT(1)," +
            "nomessage TINYINT(1));";
    private final String initMessagesTable = "CREATE TABLE IF NOT EXISTS message (" +
            "content TINYTEXT, " +
            "fromid BIGINT, " +
            "fromgroup BIGINT, " +
            "fromqqid BIGINT, " +
            "fromuuid CHAR(32), " +
            "senttime TIMESTAMP DEFAULT CURRENT_TIMESTAMP); ";
    private static final String selectPlayer = "SELECT * FROM player WHERE %s=%s LIMIT 1;";
    private static final String selectPlayerList = "SELECT * FROM player WHERE %s=%s;";
    private static final String updatePlayer = "UPDATE player SET %s=%s WHERE %s=%s;";
    private static final String insertPlayer = "INSERT OR IGNORE INTO player(%s) VALUES('%s');";
    private static final String insertMessage = "INSERT INTO message(%s) VALUES(%s);";

    SQLite(Connection playerC, @Nullable Connection messageC) {
        playerConnection = playerC;
        messageConnection = messageC;
        initTables();
    }

    /**
     * Initialize tables for sqlite.
     */
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

    /**
     * Get a player instance from database.
     *
     * @param selectColumn The column name of selecting.
     * @param selectValue  The corresponding value.
     * @return A Player instance in 3 circumstances. 1). Fully bound player, with valid id, uuid, qqid, mcname; 2). Registered but not bound
     * player, with valid id, uuid but <b>NULL qqid and mcname</b>. 3). Not registered player, all 4 columns are NULL.
     */
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
                player.close();
                return result;
            }
        } catch (SQLException e) {
            LOGGER.error(i18n("queryFail"), e.getLocalizedMessage());
            if (PhenylConfiguration.debug) e.printStackTrace();
        }
        return new Player(null, null, null, null);
    }

    private static List<Player> getPlayerList(String selectColumn, String selectValue) {
        ResultSet resultSet;
        List<Player> result = new java.util.ArrayList<>();
        try {
            player = playerConnection.createStatement();
            resultSet = player.executeQuery(String.format(selectPlayerList, selectColumn, selectValue));
            if (resultSet.isBeforeFirst()) {
                while (resultSet.next()) {
                    result.add(new Player(resultSet.getInt("id"),
                            resultSet.getString("uuid"),
                            resultSet.getString("qqid") == null ? null : Long.parseLong(resultSet.getString("qqid")),
                            resultSet.getString("mcname")));
                }
                player.close();
                return result;
            }
        } catch (SQLException e) {
            LOGGER.error(i18n("queryFail"), e.getLocalizedMessage());
            if (PhenylConfiguration.debug) e.printStackTrace();
        }
        return result;
    }

    /**
     * Update players in database.
     *
     * @param setColumn    The column to set.
     * @param setValue     The value to be set.
     * @param selectColumn The selecting column.
     * @param selectValue  The selecting value.
     * @return True - the update done successfully. False - query failed.
     */
    private static boolean updatePlayer(String setColumn, String setValue, String selectColumn, String selectValue) {
        if (!setColumn.equalsIgnoreCase("qqid")) setValue = String.format("'%s'", setValue);
        if (!selectColumn.equalsIgnoreCase("qqid") && !selectValue.equalsIgnoreCase("null"))
            selectValue = String.format("'%s'", selectValue);

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

    /**
     * Insert a player.
     *
     * @param insertColumns The column to insert into.
     * @param insertValues  The value to insert.
     * @return True - the insert query was done successfully. False - query failed.
     */
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

    /**
     * Insert a message.
     *
     * @param insertColumns The column to insert into.
     * @param insertValues  The value to insert.
     * @return True - the insert query was done successfully. False - query failed.
     */
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

    /**
     * <b>Below are methods for database operations.</b><br>
     * Try to register a player.
     *
     * @param uuid   The player's UUID.
     * @param mcname The player's Minecraft username.
     * @return Whether the queries succeeded;
     */
    static boolean registerPlayer(String uuid, String mcname) {
        if (getPlayer("uuid", uuid).id() == null)
            return insertPlayer("uuid", uuid) && updatePlayer("mcname", mcname, "uuid", uuid);
        return false;
    }

    static boolean getRegistered(String uuid) {
        return getPlayer("uuid", uuid).id() != null;
    }

    /**
     * Update the username if is null or does not match the param's.
     *
     * @param uuid     The player's Minecraft UUID.
     * @param userName The player's Minecraft username.
     * @return True - the username needs to be updated and that is done successfully. False - no need to update username or query failed.
     */
    static boolean updateUserName(String uuid, String userName) {
        Player result = getPlayer("uuid", uuid);
        if (result.mcname() == null || !result.mcname().equals(userName)) {
            return updatePlayer("mcname", userName, "uuid", uuid);
        }
        return false;
    }

    static boolean updateMutedPlayer(String uuid, Boolean toggle) {
        return updatePlayer("muted", toggle ? "1" : "0", "uuid", uuid);
    }

    static boolean updateNoMessagePlayer(String uuid, Boolean toggle) {
        return updatePlayer("nomessage", toggle ? "1" : "0", "uuid", uuid);
    }

    static List<Player> getMutedPlayer() {
        return getPlayerList("muted", "1");
    }

    static List<Player> getNoMessagePlayer() {
        return getPlayerList("nomessage", "1");
    }

    /**
     * Add a binding.
     *
     * @param uuid The player's UUID.
     * @param qqid The player's QQ ID.
     * @return True - both Minecraft username and QQ ID are successfully added to database. False - query failed.
     */
    static boolean addBinding(String uuid, Long qqid) {
        return updatePlayer("qqid", qqid.toString(), "uuid", uuid);
    }

    static boolean removeBinding(String uuid) {
        return updatePlayer("qqid", "NULL", "uuid", uuid);
    }

    /**
     * Get player by Minecraft UUID.
     *
     * @param uuid The player's UUID.
     * @return Player instance gotten from {@link #getPlayer(String, String)}.
     */
    static Player getBinding(String uuid) {
        return getPlayer("uuid", uuid);
    }

    /**
     * Get player username by QQ ID.
     *
     * @param qqid The player's QQ ID.
     * @return Player instance gotten from {@link #getPlayer(String, String)}.
     */
    static Player getBinding(Long qqid) {
        return getPlayer("qqid", qqid.toString());
    }

    /**
     * Get the player id by Minecraft username.
     *
     * @param userName The player's Minecraft username.
     * @return Corresponding id if found, null if not.
     */
    static Integer getIDByUserName(String userName) {
        return getPlayer("mcname", userName).id();
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
    static boolean addMessage(String content, Long groupID, Long qqID) {
        Player result = getPlayer("qqid", qqID.toString());
        if (result.uuid() != null)
            return insertMessage("content,fromid,fromuuid,fromgroup,fromqqid", String.format("'%s',%s,'%s',%s,%s", content, result.id(), result.uuid(), groupID.toString(), qqID));
        else
            return insertMessage("content,fromgroup,fromqqid", String.format("'%s',%s,%s", content, groupID.toString(), qqID));
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
    static boolean addMessage(String content, String fromuuid) {
        Player result = getPlayer("uuid", fromuuid);
        if (result.qqid() != null)
            return insertMessage("content,fromid,fromqqid,fromuuid", String.format("'%s',%s,%s,'%s'", content, result.id(), result.qqid(), result.uuid()));
        return insertMessage("content,fromuuid", String.format("'%s','%s'", content, fromuuid));
    }
}
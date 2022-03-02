package live.turna.phenyl.database;

import live.turna.phenyl.config.PhenylConfiguration;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static live.turna.phenyl.Phenyl.LOGGER;
import static live.turna.phenyl.message.I18n.i18n;

/**
 * <b>SQLite</b><br>
 * SQLite handler.
 *
 * @see live.turna.phenyl.database.SQLExecutor
 * @see live.turna.phenyl.database.Storage
 * @since 2021/12/5 21:08
 */
public class SQLite implements Storage {
    private Connection playerConnection;
    private Connection messageConnection;
    private Statement player;
    private Statement message;

    private final String initPlayerTable = "CREATE TABLE IF NOT EXISTS player (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL, " +
            "uuid CHAR(36), " +
            "qqid BIGINT, " +
            "mcname TINYTEXT," +
            "muted TINYINT(1)," +
            "nomessage TINYINT(1));";
    private final String initMessagesTable = "CREATE TABLE IF NOT EXISTS message (" +
            "content TEXT, " +
            "fromid BIGINT, " +
            "fromgroup BIGINT, " +
            "fromqqid BIGINT, " +
            "fromuuid CHAR(36), " +
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

    public void initTables() {
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

    public Player getPlayer(String selectColumn, String selectValue) {
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

    public List<Player> getPlayerList(String selectColumn, String selectValue) {
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

    public boolean updatePlayer(String setColumn, String setValue, String selectColumn, String selectValue) {
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

    public boolean insertPlayer(String insertColumns, String insertValues) {
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

    public boolean insertMessage(String insertColumns, String insertValues) {
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

    public void onDisable() {
        try {
            player.close();
            playerConnection.close();
            if (messageConnection != null) {
                messageConnection.close();
                message.close();
            }
        } catch (SQLException e) {
            LOGGER.error(i18n("databaseCloseFail"), e.getLocalizedMessage());
            if (PhenylConfiguration.debug) e.printStackTrace();
        }
    }
}
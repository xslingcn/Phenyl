package live.turna.phenyl.database.sql;

import live.turna.phenyl.database.PhenylStorage;
import live.turna.phenyl.database.Player;

import java.util.List;

/**
 * <b>SQLStorage</b><br>
 * Storage for SQL implementations.
 *
 * @see SQLQuery
 * @since 2022/3/4 21:18
 */
public interface SQLStorage extends SQLQuery, PhenylStorage {

    default boolean registerPlayer(String uuid, String mcname) {
        if (getPlayer("uuid", uuid).id() == null)
            return insertPlayer("uuid", uuid) && updatePlayer("mcname", mcname, "uuid", uuid);
        return false;
    }

    default boolean getRegistered(String uuid) {
        return getPlayer("uuid", uuid).id() != null;
    }

    default boolean updateUserName(String uuid, String userName) {
        Player result = getPlayer("uuid", uuid);
        if (result.mcname() == null || !result.mcname().equals(userName)) {
            return updatePlayer("mcname", userName, "uuid", uuid);
        }
        return false;
    }

    default boolean updateMutedPlayer(String uuid, Boolean toggle) {
        return updatePlayer("muted", toggle ? "1" : "0", "uuid", uuid);
    }

    default boolean updateNoMessagePlayer(String uuid, Boolean toggle) {
        return updatePlayer("nomessage", toggle ? "1" : "0", "uuid", uuid);
    }

    default List<Player> getMutedPlayer() {
        return getPlayerList("muted", "1");
    }

    default List<Player> getNoMessagePlayer() {
        return getPlayerList("nomessage", "1");
    }

    default boolean addBinding(String uuid, Long qqid) {
        return updatePlayer("qqid", qqid.toString(), "uuid", uuid);
    }

    default boolean removeBinding(String uuid) {
        return updatePlayer("qqid", "NULL", "uuid", uuid);
    }

    default Player getBinding(String uuid) {
        return getPlayer("uuid", uuid);
    }

    default Player getBinding(Long qqid) {
        return getPlayer("qqid", qqid.toString());
    }

    default Integer getIDByUserName(String userName) {
        return getPlayer("mcname", userName).id();
    }

    default boolean addMessage(String content, Long groupID, Long qqID) {
        Player result = getPlayer("qqid", qqID.toString());
        if (result.uuid() != null)
            return insertMessage("content,fromid,fromuuid,fromgroup,fromqqid", String.format("'%s',%s,'%s',%s,%s", content, result.id(), result.uuid(), groupID.toString(), qqID));
        else
            return insertMessage("content,fromgroup,fromqqid", String.format("'%s',%s,%s", content, groupID.toString(), qqID));
    }

    default boolean addMessage(String content, String fromuuid) {
        Player result = getPlayer("uuid", fromuuid);
        if (result.qqid() != null)
            return insertMessage("content,fromid,fromqqid,fromuuid", String.format("'%s',%s,%s,'%s'", content, result.id(), result.qqid(), result.uuid()));
        return insertMessage("content,fromid,fromuuid", String.format("'%s',%s,'%s'", content, result.id(), fromuuid));
    }

}

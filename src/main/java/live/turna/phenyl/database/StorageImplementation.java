package live.turna.phenyl.database;

import live.turna.phenyl.database.sql.SQLExecutor;

import java.util.List;

/**
 * Data services.
 */
public interface StorageImplementation extends SQLExecutor {
    /**
     * Try to register a player.
     *
     * @param uuid   The player's Minecraft UUID.
     * @param mcname The player's Minecraft username.
     * @return Whether the queries succeeded.
     */
    default boolean registerPlayer(String uuid, String mcname) {
        if (getPlayer("uuid", uuid).id() == null)
            return insertPlayer("uuid", uuid) && updatePlayer("mcname", mcname, "uuid", uuid);
        return false;
    }

    /**
     * Get if a player is registered.
     *
     * @param uuid The player's Minecraft UUID.
     * @return True - player is registered; False - not registered.
     */
    default boolean getRegistered(String uuid) {
        return getPlayer("uuid", uuid).id() != null;
    }

    /**
     * Update the username if is null or does not match the param's.
     *
     * @param uuid     The player's Minecraft UUID.
     * @param userName The player's Minecraft username.
     * @return True - the username needs to be updated and that is done successfully. False - no need to update username or query failed.
     */
    default boolean updateUserName(String uuid, String userName) {
        Player result = getPlayer("uuid", uuid);
        if (result.mcname() == null || !result.mcname().equals(userName)) {
            return updatePlayer("mcname", userName, "uuid", uuid);
        }
        return false;
    }

    /**
     * Update the player's muted setting.
     *
     * @param uuid   The player's Minecraft UUID.
     * @param toggle Whether is muted.
     * @return Whether the query succeeded.
     */
    default boolean updateMutedPlayer(String uuid, Boolean toggle) {
        return updatePlayer("muted", toggle ? "1" : "0", "uuid", uuid);
    }

    /**
     * Update the player's nomessage setting.
     *
     * @param uuid   The player's Minecraft UUID.
     * @param toggle Whether is muted.
     * @return Whether the query succeeded.
     */
    default boolean updateNoMessagePlayer(String uuid, Boolean toggle) {
        return updatePlayer("nomessage", toggle ? "1" : "0", "uuid", uuid);
    }

    /**
     * Get the list of muted players.
     *
     * @return An empty list if no player is muted, or a list of player instances.
     */
    default List<Player> getMutedPlayer() {
        return getPlayerList("muted", "1");
    }

    /**
     * Get the list of nomessaged players.
     *
     * @return An empty list if no player is nomessaged, or a list of player instances.
     */
    default List<Player> getNoMessagePlayer() {
        return getPlayerList("nomessage", "1");
    }

    /**
     * Add a binding.
     *
     * @param uuid The player's Minecraft UUID.
     * @param qqid The player's QQ ID.
     * @return True - both Minecraft username and QQ ID are successfully added to database. False - query failed.
     */
    default boolean addBinding(String uuid, Long qqid) {
        return updatePlayer("qqid", qqid.toString(), "uuid", uuid);
    }

    /**
     * Remove a player's QQ-UUID binding by setting the qqid column to NULL.
     *
     * @param uuid The player's Minecraft UUID.
     * @return True - The binding is successfully removed. False - query failed.
     */
    default boolean removeBinding(String uuid) {
        return updatePlayer("qqid", "NULL", "uuid", uuid);
    }

    /**
     * Get player by Minecraft UUID.
     *
     * @param uuid The player's Minecraft UUID.
     * @return Player instance gotten from {@link #getPlayer(String, String)}.
     */
    default Player getBinding(String uuid) {
        return getPlayer("uuid", uuid);
    }

    /**
     * Get player username by QQ ID.
     *
     * @param qqid The player's QQ ID.
     * @return Player instance gotten from {@link #getPlayer(String, String)}.
     */
    default Player getBinding(Long qqid) {
        return getPlayer("qqid", qqid.toString());
    }

    /**
     * Get the player id by Minecraft username.
     *
     * @param userName The player's Minecraft username.
     * @return Corresponding id if found, null if not.
     */
    default Integer getIDByUserName(String userName) {
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
    default boolean addMessage(String content, Long groupID, Long qqID) {
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
    default boolean addMessage(String content, String fromuuid) {
        Player result = getPlayer("uuid", fromuuid);
        if (result.qqid() != null)
            return insertMessage("content,fromid,fromqqid,fromuuid", String.format("'%s',%s,%s,'%s'", content, result.id(), result.qqid(), result.uuid()));
        return insertMessage("content,fromid,fromuuid", String.format("'%s',%s,'%s'", content, result.id(), fromuuid));
    }

    /**
     * Close all connections.
     */
    void onDisable();
}

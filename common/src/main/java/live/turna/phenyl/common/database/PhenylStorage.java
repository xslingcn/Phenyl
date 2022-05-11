package live.turna.phenyl.common.database;

import java.util.List;

/**
 * <b>PhenylStorage</b><br>
 * Defines all operations that should be supported for Phenyl storage.
 *
 * @since 2022/3/29 20:52
 */
public interface PhenylStorage {
    /**
     * Try to register a player.
     *
     * @param uuid   The player's Minecraft UUID.
     * @param mcname The player's Minecraft username.
     * @return Whether the queries succeeded.
     */
    boolean registerPlayer(String uuid, String mcname);

    /**
     * Get if a player is registered.
     *
     * @param uuid The player's Minecraft UUID.
     * @return True - player is registered; False - not registered.
     */
    boolean getRegistered(String uuid);

    /**
     * Update the username if is null or does not match the param's.
     *
     * @param uuid     The player's Minecraft UUID.
     * @param userName The player's Minecraft username.
     * @return True - the username needs to be updated and that is done successfully. False - no need to update username or query failed.
     */
    boolean updateUserName(String uuid, String userName);

    /**
     * Update the player's muted setting.
     *
     * @param uuid   The player's Minecraft UUID.
     * @param toggle Whether is muted.
     * @return Whether the query succeeded.
     */
    boolean updateMutedPlayer(String uuid, Boolean toggle);

    /**
     * Update the player's nomessage setting.
     *
     * @param uuid   The player's Minecraft UUID.
     * @param toggle Whether is muted.
     * @return Whether the query succeeded.
     */
    boolean updateNoMessagePlayer(String uuid, Boolean toggle);

    /**
     * Get the list of muted players.
     *
     * @return An empty list if no player is muted, or a list of player instances.
     */
    List<Player> getMutedPlayer();

    /**
     * Get the list of nomessaged players.
     *
     * @return An empty list if no player is nomessaged, or a list of player instances.
     */
    List<Player> getNoMessagePlayer();

    /**
     * Get the list of all bound players;
     *
     * @return A list of player instances, of which would never hold a NULL qqid. Could be empty if no one had bound yet.
     */
    List<Player> getAllBoundPlayer();

    /**
     * Add a binding.
     *
     * @param uuid The player's Minecraft UUID.
     * @param qqid The player's QQ ID.
     * @return True - both Minecraft username and QQ ID are successfully added to database. False - query failed.
     */
    boolean addBinding(String uuid, Long qqid);

    /**
     * Remove a player's QQ-UUID binding by setting the qqid column to NULL.
     *
     * @param uuid The player's Minecraft UUID.
     * @return True - The binding is successfully removed. False - query failed.
     */
    boolean removeBinding(String uuid);

    /**
     * Get player by Minecraft UUID.
     *
     * @param uuid The player's Minecraft UUID.
     * @return Player instance.
     */
    Player getBinding(String uuid);

    /**
     * Get player username by QQ ID.
     *
     * @param qqid The player's QQ ID.
     * @return Player instance.
     */
    Player getBinding(Long qqid);

    /**
     * Get the player id by Minecraft username.
     *
     * @param userName The player's Minecraft username.
     * @return Corresponding id if found, null if not.
     */
    Integer getIDByUserName(String userName);

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
    boolean addMessage(String content, Long groupID, Long qqID);

    /**
     * Add a message from Minecraft chat.
     * Phenyl will try to find the sender's binding first. If succeeded, the player's id and QQ ID would be attached as well.
     * If not, only the player's UUID would be added.
     *
     * @param content  message content.
     * @param fromuuid The sender's Minecraft UUID.
     * @return True - the insert query was done successfully. False - query failed.
     */
    boolean addMessage(String content, String fromuuid);

    /**
     * Close all connections.
     */
    void shutdown();
}

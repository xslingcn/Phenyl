package live.turna.phenyl.mirai.event

/**
 * **CBotOfflineEvent**<br></br>
 * BotOfflineEvent class.
 *
 * @since 2021/12/4 4:19
 */
interface PBotOfflineEvent {
    /**
     * Get the type of dropping cause.<br></br>
     * Possible results:<br></br>
     * `Active`, `Force`, `MsfOffline`, `Dropped`, `RequireReconnect`.
     *
     * @see net.mamoe.mirai.event.events.BotOfflineEvent
     */

    val type: String
    val id: Long
}
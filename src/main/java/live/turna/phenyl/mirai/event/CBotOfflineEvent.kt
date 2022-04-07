package live.turna.phenyl.mirai.event

import net.mamoe.mirai.event.events.BotOfflineEvent
import net.md_5.bungee.api.plugin.Event

/**
 * **CBotOfflineEvent**<br></br>
 * BotOfflineEvent class.
 *
 * @since 2021/12/4 4:19
 */
class CBotOfflineEvent(event: BotOfflineEvent) : Event() {
    /**
     * Get the type of dropping cause.<br></br>
     * Possible results:<br></br>
     * `Active`, `Force`, `MsfOffline`, `Dropped`, `RequireReconnect`.
     *
     * @see net.mamoe.mirai.event.events.BotOfflineEvent
     */

    val type :String =event.javaClass.toString().split("\$").toTypedArray()[1]
    val id :Long = event.bot.id
}
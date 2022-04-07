package live.turna.phenyl.mirai.event

import net.mamoe.mirai.event.events.UserMessageEvent
import net.md_5.bungee.api.plugin.Event

/**
 * **CUserMessageEvent**<br></br>
 *
 * @since 2021/12/4 5:00
 */
class CUserMessageEvent(private val event: UserMessageEvent) : Event()
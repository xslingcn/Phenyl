package live.turna.phenyl.mirai.event

import net.mamoe.mirai.event.events.MemberLeaveEvent
import net.md_5.bungee.api.plugin.Event

/**
 * **CMemberLeaveEvent**<br></br>
 *
 * @since 2021/12/4 4:59
 */
class CMemberLeaveEvent(private val event: MemberLeaveEvent) : Event()
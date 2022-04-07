package live.turna.phenyl.mirai.event

import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.MessageChain
import net.md_5.bungee.api.plugin.Event

/**
 * **CGroupMessageEvent**<br></br>
 * GroupMessageEvent wrapper class.
 *
 * @since 2021/12/4 4:59
 */
class CGroupMessageEvent(private val event: GroupMessageEvent) : Event() {
    val group: Group = event.group

    val groupName : String = event.group.name

    val groupId: Long = event.group.id

    val senderId: Long = event.sender.id

    /**
     * Get the message sender's in-group name card. If not found, return the message sender's nickname.
     */
    val senderNameCardOrNick:String = if (event.sender.nameCard.isEmpty()) event.sender.nick else event.sender.nameCard

    val message: MessageChain=event.message

    val messageString:String=event.message.contentToString()

    val messageMiraiCode:String=event.message.serializeToMiraiCode()
}
package live.turna.phenyl.mirai.event

import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.data.MessageChain

/**
 * **CGroupMessageEvent**<br></br>
 * GroupMessageEvent wrapper class.
 *
 * @since 2021/12/4 4:59
 */
interface PGroupMessageEvent {
    val group: Group

    val groupName: String

    val groupId: Long

    val senderId: Long

    /**
     * Get the message sender's in-group name card. If not found, return the message sender's nickname.
     */
    val senderNameCardOrNick: String

    val message: MessageChain

    val messageString: String

    val messageMiraiCode: String
}
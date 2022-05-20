package live.turna.phenyl.velocity.event

import live.turna.phenyl.mirai.event.PGroupMessageEvent
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.MessageChain

class VelocityGroupMessageEvent(private val event: GroupMessageEvent) : PGroupMessageEvent {
    override val group: Group
        get() = event.group

    override val groupName: String
        get() = event.group.name

    override val groupId: Long
        get() = event.group.id

    override val senderId: Long
        get() = event.sender.id

    override val senderNameCardOrNick: String
        get() = event.sender.nameCard.ifEmpty { event.sender.nick }

    override val message: MessageChain
        get() = event.message

    override val messageString: String
        get() = event.message.contentToString()

    override val messageMiraiCode: String
        get() = event.message.serializeToMiraiCode()
}
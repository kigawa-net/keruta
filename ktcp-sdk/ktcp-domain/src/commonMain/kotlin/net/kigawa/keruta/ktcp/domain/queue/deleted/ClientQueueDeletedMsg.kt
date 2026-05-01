package net.kigawa.keruta.ktcp.domain.queue.deleted

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.domain.msg.client.ClientMsg
import net.kigawa.keruta.ktcp.domain.msg.client.ClientMsgType

@Serializable
data class ClientQueueDeletedMsg(
    override val type: ClientMsgType = ClientMsgType.QUEUE_DELETED,
    val id: Long,
) : ClientMsg {
    init {
        require(type == ClientMsgType.QUEUE_DELETED) { "type must be QUEUE_DELETED" }
    }
}

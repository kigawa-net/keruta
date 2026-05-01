package net.kigawa.keruta.ktcp.domain.queue.updated

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.domain.msg.client.ClientMsg
import net.kigawa.keruta.ktcp.domain.msg.client.ClientMsgType

@Serializable
data class ClientQueueUpdatedMsg(
    override val type: ClientMsgType = ClientMsgType.QUEUE_UPDATED,
    val id: Long,
    val name: String,
): ClientMsg {
    init {
        require(type == ClientMsgType.QUEUE_UPDATED) { "type must be QUEUE_UPDATED" }
    }
}
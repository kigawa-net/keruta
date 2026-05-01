package net.kigawa.keruta.ktcp.domain.queue.created

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.domain.msg.client.ClientMsg
import net.kigawa.keruta.ktcp.domain.msg.client.ClientMsgType

@Serializable
data class ClientQueueCreatedMsg(
    override val type: ClientMsgType = ClientMsgType.QUEUE_CREATED,
    val queueId: Long,
): ClientMsg {
    init {
        require(type == ClientMsgType.QUEUE_CREATED) { "type must be QUEUE_CREATED" }
    }
}

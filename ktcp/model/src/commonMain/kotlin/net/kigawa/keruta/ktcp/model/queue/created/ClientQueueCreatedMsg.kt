package net.kigawa.keruta.ktcp.model.queue.created

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.model.msg.client.ClientMsg
import net.kigawa.keruta.ktcp.model.msg.client.ClientMsgType

@Serializable
data class ClientQueueCreatedMsg(
    override val type: ClientMsgType = ClientMsgType.QUEUE_CREATED,
): ClientMsg {
    init {
        require(type == ClientMsgType.QUEUE_CREATED) { "type must be QUEUE_CREATED" }
    }
}

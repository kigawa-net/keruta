package net.kigawa.keruta.ktcp.domain.queue.listed

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.domain.msg.client.ClientMsg
import net.kigawa.keruta.ktcp.domain.msg.client.ClientMsgType

@Serializable
data class ClientQueueListedMsg(
    override val type: ClientMsgType = ClientMsgType.QUEUE_LISTED,
    val queues: List<Queue>,
): ClientMsg {
    init {
        require(type == ClientMsgType.QUEUE_LISTED) { "type must be QUEUE_LISTED" }
    }

    @Serializable
    class Queue(
        val name: String, val id: Long, val providerId: Long,
    )
}

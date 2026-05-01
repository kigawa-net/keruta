package net.kigawa.keruta.ktcp.domain.queue.showed

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.domain.msg.client.ClientMsg
import net.kigawa.keruta.ktcp.domain.msg.client.ClientMsgType

@Serializable
data class ClientQueueShowedMsg(
    override val type: ClientMsgType = ClientMsgType.QUEUE_SHOWED,
    val name: String,
    val id: Long,
    val setting: String,
): ClientMsg {
    init {
        require(type == ClientMsgType.QUEUE_SHOWED) { "type must be QUEUE_SHOWED" }
    }

}

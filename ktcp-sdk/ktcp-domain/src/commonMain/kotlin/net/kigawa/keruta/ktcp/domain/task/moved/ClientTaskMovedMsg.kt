package net.kigawa.keruta.ktcp.domain.task.moved

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.domain.msg.client.ClientMsg
import net.kigawa.keruta.ktcp.domain.msg.client.ClientMsgType

@Serializable
data class ClientTaskMovedMsg(
    override val type: ClientMsgType = ClientMsgType.TASK_MOVED,
    val taskId: Long,
    val queueId: Long,
): ClientMsg {
    init {
        require(type == ClientMsgType.TASK_MOVED) { "type must be TASK_MOVED" }
    }
}

package net.kigawa.keruta.ktcp.model.task.moved

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.model.msg.client.ClientMsg
import net.kigawa.keruta.ktcp.model.msg.client.ClientMsgType

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
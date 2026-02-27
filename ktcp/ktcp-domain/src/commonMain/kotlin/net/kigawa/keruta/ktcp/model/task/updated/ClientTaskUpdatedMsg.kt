package net.kigawa.keruta.ktcp.model.task.updated

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.model.msg.client.ClientMsg
import net.kigawa.keruta.ktcp.model.msg.client.ClientMsgType

@Serializable
data class ClientTaskUpdatedMsg(
    override val type: ClientMsgType = ClientMsgType.TASK_UPDATED,
    val id: Long,
    val status: String,
): ClientMsg {
    init {
        require(type == ClientMsgType.TASK_UPDATED) { "type must be TASK_UPDATED" }
    }
}
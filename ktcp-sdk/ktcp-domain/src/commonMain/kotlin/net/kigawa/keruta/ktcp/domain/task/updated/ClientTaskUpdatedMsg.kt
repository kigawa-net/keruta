package net.kigawa.keruta.ktcp.domain.task.updated

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.domain.msg.client.ClientMsg
import net.kigawa.keruta.ktcp.domain.msg.client.ClientMsgType

@Serializable
data class ClientTaskUpdatedMsg(
    override val type: ClientMsgType = ClientMsgType.TASK_UPDATED,
    val id: Long,
    val status: String,
    val log: String? = null,
): ClientMsg {
    init {
        require(type == ClientMsgType.TASK_UPDATED) { "type must be TASK_UPDATED" }
    }
}

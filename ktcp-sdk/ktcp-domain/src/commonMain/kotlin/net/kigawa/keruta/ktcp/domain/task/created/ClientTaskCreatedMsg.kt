package net.kigawa.keruta.ktcp.domain.task.created

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.domain.msg.client.ClientMsg
import net.kigawa.keruta.ktcp.domain.msg.client.ClientMsgType

@Serializable
data class ClientTaskCreatedMsg(
    override val type: ClientMsgType = ClientMsgType.TASK_CREATED,
    val id: Long,
): ClientMsg {
    init {
        require(type == ClientMsgType.TASK_CREATED) { "type must be TASK_CREATED" }
    }
}

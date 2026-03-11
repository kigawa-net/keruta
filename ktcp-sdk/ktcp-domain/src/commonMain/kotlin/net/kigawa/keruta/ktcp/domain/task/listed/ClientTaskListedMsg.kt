package net.kigawa.keruta.ktcp.domain.task.listed

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.domain.msg.client.ClientMsg
import net.kigawa.keruta.ktcp.domain.msg.client.ClientMsgType

@Serializable
data class ClientTaskListedMsg(
    override val type: ClientMsgType = ClientMsgType.TASK_LISTED,
    val tasks: List<Task>,
): ClientMsg {
    init {
        require(type == ClientMsgType.TASK_LISTED) { "type must be TASK_LISTED" }
    }

    @Serializable
    data class Task(val title: String, val id: Long, val description: String, val status: String)
}

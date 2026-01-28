package net.kigawa.keruta.ktcp.model.task.listed

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.model.msg.client.ClientMsg
import net.kigawa.keruta.ktcp.model.msg.client.ClientMsgType

@Serializable
data class ClientTaskListedMsg(
    override val type: ClientMsgType = ClientMsgType.TASK_LISTED,
    val tasks: List<Task>,
): ClientMsg {
    init {
        require(type == ClientMsgType.TASK_LISTED) { "type must be TASK_LISTED" }
    }

    @Serializable
    data class Task(val name: String, val id: Long)
}

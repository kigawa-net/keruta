package net.kigawa.keruta.ktcp.model.task.showed

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.model.msg.client.ClientMsg
import net.kigawa.keruta.ktcp.model.msg.client.ClientMsgType

@Serializable
data class ClientTaskShowedMsg(
    override val type: ClientMsgType = ClientMsgType.TASK_SHOWED,
    val title: String, val id: Long, val description: String,
): ClientMsg {
    init {
        require(type == ClientMsgType.TASK_SHOWED) { "type must be TASK_SHOWED" }
    }

}

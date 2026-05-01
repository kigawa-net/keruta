package net.kigawa.keruta.ktcp.domain.task.showed

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.domain.msg.client.ClientMsg
import net.kigawa.keruta.ktcp.domain.msg.client.ClientMsgType

@Serializable
data class ClientTaskShowedMsg(
    override val type: ClientMsgType = ClientMsgType.TASK_SHOWED,
    val title: String, val id: Long, val description: String,
    val status: String = "",
    val log: String? = null,
): ClientMsg {
    init {
        require(type == ClientMsgType.TASK_SHOWED) { "type must be TASK_SHOWED" }
    }

}

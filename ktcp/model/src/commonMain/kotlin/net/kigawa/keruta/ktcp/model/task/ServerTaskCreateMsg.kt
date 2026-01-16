package net.kigawa.keruta.ktcp.model.task

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.model.msg.ServerMsg
import net.kigawa.keruta.ktcp.model.msg.ServerMsgType


@Serializable
data class ServerTaskCreateMsg(
    override val type: ServerMsgType = ServerMsgType.TASK_CREATE,
    val name: String,
    val queueId: String,
): ServerMsg {
    init {
        require(type == ServerMsgType.TASK_CREATE)
    }
}

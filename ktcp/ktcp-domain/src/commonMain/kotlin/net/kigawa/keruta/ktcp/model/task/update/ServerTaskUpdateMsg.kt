package net.kigawa.keruta.ktcp.model.task.update

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.model.msg.server.ServerMsg
import net.kigawa.keruta.ktcp.model.msg.server.ServerMsgType


@Serializable
data class ServerTaskUpdateMsg(
    override val type: ServerMsgType = ServerMsgType.TASK_UPDATE,
    val taskId: Long,
    val status: String,
): ServerMsg {
    init {
        require(type == ServerMsgType.TASK_UPDATE)
    }
}
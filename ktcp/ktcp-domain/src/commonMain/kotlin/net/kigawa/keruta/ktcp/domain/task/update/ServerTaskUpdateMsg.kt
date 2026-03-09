package net.kigawa.keruta.ktcp.domain.task.update

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.domain.msg.server.ServerMsg
import net.kigawa.keruta.ktcp.domain.msg.server.ServerMsgType


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

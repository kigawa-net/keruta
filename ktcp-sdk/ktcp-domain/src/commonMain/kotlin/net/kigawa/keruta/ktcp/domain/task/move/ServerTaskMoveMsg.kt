package net.kigawa.keruta.ktcp.domain.task.move

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.domain.msg.server.ServerMsg
import net.kigawa.keruta.ktcp.domain.msg.server.ServerMsgType


@Serializable
data class ServerTaskMoveMsg(
    override val type: ServerMsgType = ServerMsgType.TASK_MOVE,
    val taskId: Long,
    val targetQueueId: Long,
): ServerMsg {
    init {
        require(type == ServerMsgType.TASK_MOVE)
    }
}

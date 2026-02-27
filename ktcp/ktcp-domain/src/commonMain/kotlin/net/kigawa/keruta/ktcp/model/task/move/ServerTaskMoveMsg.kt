package net.kigawa.keruta.ktcp.model.task.move

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.model.msg.server.ServerMsg
import net.kigawa.keruta.ktcp.model.msg.server.ServerMsgType


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
package net.kigawa.keruta.ktcp.model.task.list

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.model.msg.server.ServerMsg
import net.kigawa.keruta.ktcp.model.msg.server.ServerMsgType

@Serializable
data class ServerTaskListMsg(
    override val type: ServerMsgType = ServerMsgType.TASK_LIST,
    val queueId: Long,
): ServerMsg {
    init {
        require(type == ServerMsgType.TASK_LIST) { "type must be TASK_LIST" }
    }
}

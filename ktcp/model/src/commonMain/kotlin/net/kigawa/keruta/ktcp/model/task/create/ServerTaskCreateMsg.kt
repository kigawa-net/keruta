package net.kigawa.keruta.ktcp.model.task.create

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.model.msg.server.ServerMsg
import net.kigawa.keruta.ktcp.model.msg.server.ServerMsgType


@Serializable
data class ServerTaskCreateMsg(
    override val type: ServerMsgType = ServerMsgType.TASK_CREATE,
    val title: String,
    val description: String,
    val queueId: Long,
): ServerMsg {
    init {
        require(type == ServerMsgType.TASK_CREATE)
    }
}

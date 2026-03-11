package net.kigawa.keruta.ktcp.domain.task.show

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.domain.msg.server.ServerMsg
import net.kigawa.keruta.ktcp.domain.msg.server.ServerMsgType

@Serializable
data class ServerTaskShowMsg(
    override val type: ServerMsgType = ServerMsgType.TASK_SHOW,
    val queueId: Long, val id: Long,
): ServerMsg {



    init {
        require(type == ServerMsgType.TASK_SHOW) { "type must be TASK_SHOW" }
    }
}

package net.kigawa.keruta.ktcp.domain.queue.show

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.domain.msg.server.ServerMsg
import net.kigawa.keruta.ktcp.domain.msg.server.ServerMsgType

@Serializable
data class ServerQueueShowMsg(
    override val type: ServerMsgType = ServerMsgType.QUEUE_SHOW,
    val id: Long,
): ServerMsg {

    init {
        require(type == ServerMsgType.QUEUE_SHOW) { "type must be QUEUE_SHOW" }
    }
}

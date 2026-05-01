package net.kigawa.keruta.ktcp.domain.queue.update

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.domain.msg.server.ServerMsg
import net.kigawa.keruta.ktcp.domain.msg.server.ServerMsgType

@Serializable
data class ServerQueueUpdateMsg(
    override val type: ServerMsgType = ServerMsgType.QUEUE_UPDATE,
    val queueId: Long,
    val name: String,
): ServerMsg {
    init {
        require(type == ServerMsgType.QUEUE_UPDATE) { "type must be QUEUE_UPDATE" }
    }
}
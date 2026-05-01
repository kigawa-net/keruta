package net.kigawa.keruta.ktcp.domain.queue.delete

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.domain.msg.server.ServerMsg
import net.kigawa.keruta.ktcp.domain.msg.server.ServerMsgType

@Serializable
data class ServerQueueDeleteMsg(
    override val type: ServerMsgType = ServerMsgType.QUEUE_DELETE,
    val queueId: Long,
) : ServerMsg {
    init {
        require(type == ServerMsgType.QUEUE_DELETE) { "type must be QUEUE_DELETE" }
    }
}

package net.kigawa.keruta.ktcp.domain.queue.create

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.domain.msg.server.ServerMsg
import net.kigawa.keruta.ktcp.domain.msg.server.ServerMsgType

@Serializable
data class ServerQueueCreateMsg(
    override val type: ServerMsgType = ServerMsgType.QUEUE_CREATE,
    val providerId: Long,
    val name: String,
): ServerMsg {
    init {
        require(type == ServerMsgType.QUEUE_CREATE) { "type must be QUEUE_CREATE" }
    }
}

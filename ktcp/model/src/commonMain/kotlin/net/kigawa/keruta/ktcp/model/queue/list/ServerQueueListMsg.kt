package net.kigawa.keruta.ktcp.model.queue.list

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.model.msg.server.ServerMsg
import net.kigawa.keruta.ktcp.model.msg.server.ServerMsgType

@Serializable
data class ServerQueueListMsg(
    override val type: ServerMsgType = ServerMsgType.QUEUE_LIST,
): ServerMsg {
    init {
        require(type == ServerMsgType.QUEUE_LIST) { "type must be QUEUE_LIST" }
    }
}

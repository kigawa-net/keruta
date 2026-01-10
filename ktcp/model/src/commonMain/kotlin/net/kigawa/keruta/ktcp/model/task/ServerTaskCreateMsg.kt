package net.kigawa.keruta.ktcp.model.task

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.model.msg.KtcpMsg
import net.kigawa.keruta.ktcp.model.msg.ServerMsgType


@Serializable
data class ServerTaskCreateMsg(
    override val type: ServerMsgType = ServerMsgType.TASK_CREATE,
): KtcpMsg {
    init {
        require(type == ServerMsgType.TASK_CREATE)
    }
}

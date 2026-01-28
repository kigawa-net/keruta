package net.kigawa.keruta.ktcp.model.provider.list

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.model.msg.server.ServerMsg
import net.kigawa.keruta.ktcp.model.msg.server.ServerMsgType

@Serializable
data class ServerProviderListMsg(
    override val type: ServerMsgType = ServerMsgType.PROVIDER_LIST,
): ServerMsg {
    init {
        require(type == ServerMsgType.PROVIDER_LIST) { "type must be PROVIDER_REQUEST" }
    }
}

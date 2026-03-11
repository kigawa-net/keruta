package net.kigawa.keruta.ktcp.domain.provider.list

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.domain.msg.server.ServerMsg
import net.kigawa.keruta.ktcp.domain.msg.server.ServerMsgType

@Serializable
data class ServerProviderListMsg(
    override val type: ServerMsgType = ServerMsgType.PROVIDER_LIST,
): ServerMsg {
    init {
        require(type == ServerMsgType.PROVIDER_LIST) { "type must be PROVIDER_REQUEST" }
    }
}

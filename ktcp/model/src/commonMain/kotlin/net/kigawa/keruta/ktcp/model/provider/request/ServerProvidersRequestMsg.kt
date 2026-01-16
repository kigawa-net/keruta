package net.kigawa.keruta.ktcp.model.provider.request

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.model.msg.server.ServerMsg
import net.kigawa.keruta.ktcp.model.msg.server.ServerMsgType

@Serializable
data class ServerProvidersRequestMsg(
    override val type: ServerMsgType = ServerMsgType.PROVIDERS_REQUEST,
): ServerMsg {
    init {
        require(type == ServerMsgType.PROVIDERS_REQUEST) { "type must be PROVIDER_REQUEST" }
    }
}

package net.kigawa.keruta.ktcp.model.provider.add

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.model.msg.server.ServerMsg
import net.kigawa.keruta.ktcp.model.msg.server.ServerMsgType

@Serializable
data class ServerProviderAddMsg(
    override val type: ServerMsgType = ServerMsgType.PROVIDER_ADD,
    val name: String,
    val issuer: String,
) : ServerMsg {
    init {
        require(type == ServerMsgType.PROVIDER_ADD) { "type must be PROVIDER_ADD" }
    }
}

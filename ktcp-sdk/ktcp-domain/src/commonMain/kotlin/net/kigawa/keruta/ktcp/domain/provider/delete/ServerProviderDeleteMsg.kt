package net.kigawa.keruta.ktcp.domain.provider.delete

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.domain.msg.server.ServerMsg
import net.kigawa.keruta.ktcp.domain.msg.server.ServerMsgType

@Serializable
data class ServerProviderDeleteMsg(
    override val type: ServerMsgType = ServerMsgType.PROVIDER_DELETE,
    val id: Long,
) : ServerMsg {
    init {
        require(type == ServerMsgType.PROVIDER_DELETE) { "type must be PROVIDER_DELETE" }
    }
}

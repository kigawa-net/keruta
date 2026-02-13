package net.kigawa.keruta.ktcp.model.provider.create

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.model.msg.server.ServerMsg
import net.kigawa.keruta.ktcp.model.msg.server.ServerMsgType

@Serializable
data class ServerProviderCreateMsg(
    override val type: ServerMsgType = ServerMsgType.PROVIDER_CREATE,
    val name: String,
    val issuer: String,
    val audience: String,
    val idps: List<Idp> = emptyList(),
): ServerMsg {
    init {
        require(type == ServerMsgType.PROVIDER_CREATE) { "type must be PROVIDER_CREATE" }
    }

    @Serializable
    data class Idp(
        val issuer: String,
        val subject: String,
        val audience: String,
    )
}

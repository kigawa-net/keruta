package net.kigawa.keruta.ktcp.model.provider.complete

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.model.msg.server.ServerMsg
import net.kigawa.keruta.ktcp.model.msg.server.ServerMsgType

@Serializable
data class ServerProviderCompleteMsg(
    override val type: ServerMsgType = ServerMsgType.PROVIDER_COMPLETE,
    val token: String,
    val code: String,
    val redirectUri: String,
) : ServerMsg {
    init {
        require(type == ServerMsgType.PROVIDER_COMPLETE) { "type must be PROVIDER_COMPLETE" }
    }
}
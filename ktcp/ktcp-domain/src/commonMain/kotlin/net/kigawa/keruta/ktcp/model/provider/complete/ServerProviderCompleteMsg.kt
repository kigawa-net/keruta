package net.kigawa.keruta.ktcp.model.provider.complete

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.model.msg.server.ServerMsg
import net.kigawa.keruta.ktcp.model.msg.server.ServerMsgType

@Serializable
data class ServerProviderCompleteMsg(
    override val type: ServerMsgType = ServerMsgType.PROVIDER_COMPLETE,
    val registerToken: String,
    val userToken: String,
    val serverToken: String,
    val userAudience: String,
    val providerAudience: String,
    val providerName: String,
): ServerMsg {
    init {
        require(type == ServerMsgType.PROVIDER_COMPLETE) { "type must be PROVIDER_COMPLETE" }
    }
}

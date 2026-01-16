package net.kigawa.keruta.ktcp.model.auth.request

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.model.auth.AuthToken
import net.kigawa.keruta.ktcp.model.msg.server.ServerMsg
import net.kigawa.keruta.ktcp.model.msg.server.ServerMsgType

/**
 * Marker interface for authentication messages.
 */
@Serializable
data class ServerAuthRequestMsg(
    val userToken: AuthToken,
    override val type: ServerMsgType = ServerMsgType.AUTH_REQUEST,
    val serverToken: String,
): ServerMsg {
    init {
        require(type == ServerMsgType.AUTH_REQUEST)
    }
}

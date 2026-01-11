package net.kigawa.keruta.ktcp.model.auth.request

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.model.auth.AuthToken
import net.kigawa.keruta.ktcp.model.msg.KtcpMsg
import net.kigawa.keruta.ktcp.model.msg.ServerMsgType

/**
 * Marker interface for authentication messages.
 */
@Serializable
data class ServerAuthRequestMsg(
    val token: AuthToken, override val type: ServerMsgType = ServerMsgType.AUTH_REQUEST,
): KtcpMsg {
    init {
        require(type == ServerMsgType.AUTH_REQUEST)
    }
}

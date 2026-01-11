package net.kigawa.keruta.ktcp.model.auth.sccess

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.model.msg.KtcpMsg
import net.kigawa.keruta.ktcp.model.msg.ServerMsgType

/**
 * Marker interface for authentication messages.
 */
@Serializable
data class AuthSuccessMsg(
    override val type: ServerMsgType = ServerMsgType.AUTH_SUCCESS,
): KtcpMsg {
    init {
        require(type == ServerMsgType.AUTH_SUCCESS)
    }
}

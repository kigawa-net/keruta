package net.kigawa.keruta.ktcp.model.authenticate

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.model.msg.KtcpMsg
import net.kigawa.keruta.ktcp.model.msg.MsgType

/**
 * Marker interface for authentication messages.
 */
@Serializable
data class AuthenticateMsg(
    val token: AuthenticateToken, override val type: MsgType,
): KtcpMsg {
    init {
        require(type == MsgType.AUTHENTICATE)
    }
}

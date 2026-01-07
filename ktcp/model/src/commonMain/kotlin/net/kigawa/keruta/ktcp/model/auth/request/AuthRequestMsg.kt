package net.kigawa.keruta.ktcp.model.auth.request

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.model.auth.AuthToken
import net.kigawa.keruta.ktcp.model.msg.KtcpMsg
import net.kigawa.keruta.ktcp.model.msg.MsgType

/**
 * Marker interface for authentication messages.
 */
@Serializable
data class AuthRequestMsg(
    val token: AuthToken, override val type: MsgType,
): KtcpMsg {
    init {
        require(type == MsgType.AUTHENTICATE)
    }
}

package net.kigawa.keruta.ktcp.model.auth.sccess

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.model.msg.KtcpMsg
import net.kigawa.keruta.ktcp.model.msg.MsgType

/**
 * Marker interface for authentication messages.
 */
@Serializable
data class AuthSuccessMsg(
    override val type: MsgType = MsgType.AUTH_SUCCESS,
): KtcpMsg {
    init {
        require(type == MsgType.AUTH_REQUEST)
    }
}

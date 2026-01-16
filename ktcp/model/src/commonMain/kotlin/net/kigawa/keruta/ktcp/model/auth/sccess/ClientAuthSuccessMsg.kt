package net.kigawa.keruta.ktcp.model.auth.sccess

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.model.msg.server.ServerMsg
import net.kigawa.keruta.ktcp.model.msg.server.ServerMsgType

/**
 * Marker interface for authentication messages.
 */
@Serializable
data class ClientAuthSuccessMsg(
    override val type: ServerMsgType = ServerMsgType.AUTH_SUCCESS,
): ServerMsg {
    init {
        require(type == ServerMsgType.AUTH_SUCCESS)
    }
}

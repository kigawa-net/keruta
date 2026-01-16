package net.kigawa.keruta.ktcp.model.provider.list

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.model.msg.ClientMsg
import net.kigawa.keruta.ktcp.model.msg.ClientMsgType

@Serializable
data class ClientProviderListMsg(
    override val type: ClientMsgType = ClientMsgType.PROVIDER_LIST,
): ClientMsg {
    init {
        require(type == ClientMsgType.PROVIDER_LIST) { "type must be PROVIDER_LIST" }
    }
}

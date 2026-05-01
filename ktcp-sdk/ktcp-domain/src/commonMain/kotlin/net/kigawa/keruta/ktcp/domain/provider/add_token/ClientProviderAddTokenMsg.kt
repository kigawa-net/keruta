package net.kigawa.keruta.ktcp.domain.provider.add_token

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.domain.msg.client.ClientMsg
import net.kigawa.keruta.ktcp.domain.msg.client.ClientMsgType

@Serializable
data class ClientProviderAddTokenMsg(
    override val type: ClientMsgType = ClientMsgType.PROVIDER_ADD_TOKEN_ISSUED,
    val token: String,
) : ClientMsg {
    init {
        require(type == ClientMsgType.PROVIDER_ADD_TOKEN_ISSUED) { "type must be PROVIDER_ADD_TOKEN_ISSUED" }
    }
}

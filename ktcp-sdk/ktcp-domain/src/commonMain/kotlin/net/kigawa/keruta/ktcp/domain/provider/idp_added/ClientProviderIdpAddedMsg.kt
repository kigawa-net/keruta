package net.kigawa.keruta.ktcp.domain.provider.idp_added

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.domain.msg.client.ClientMsg
import net.kigawa.keruta.ktcp.domain.msg.client.ClientMsgType

@Serializable
data class ClientProviderIdpAddedMsg(
    override val type: ClientMsgType = ClientMsgType.PROVIDER_IDP_ADDED,
) : ClientMsg {
    init {
        require(type == ClientMsgType.PROVIDER_IDP_ADDED) { "type must be PROVIDER_IDP_ADDED" }
    }
}

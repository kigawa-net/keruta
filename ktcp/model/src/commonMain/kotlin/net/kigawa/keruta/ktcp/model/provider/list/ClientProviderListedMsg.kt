package net.kigawa.keruta.ktcp.model.provider.list

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.model.msg.client.ClientMsg
import net.kigawa.keruta.ktcp.model.msg.client.ClientMsgType

@Serializable
data class ClientProviderListedMsg(
    override val type: ClientMsgType = ClientMsgType.PROVIDER_LISTED,
    val providers: List<Provider>,
): ClientMsg {
    init {
        require(type == ClientMsgType.PROVIDER_LISTED) { "type must be PROVIDER_LIST" }
    }

    @Serializable
    class Provider(
        val name: String, val id: Long,
        val issuer: String, val audience: String,
    )
}

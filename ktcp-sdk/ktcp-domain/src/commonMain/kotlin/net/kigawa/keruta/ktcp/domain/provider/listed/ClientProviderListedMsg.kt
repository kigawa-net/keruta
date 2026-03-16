package net.kigawa.keruta.ktcp.domain.provider.listed

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.domain.msg.client.ClientMsg
import net.kigawa.keruta.ktcp.domain.msg.client.ClientMsgType

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
        val idps: List<Idp> = emptyList(),
    )

    @Serializable
    class Idp(
        val issuer: String,
        val subject: String,
        val audience: String,
    )
}

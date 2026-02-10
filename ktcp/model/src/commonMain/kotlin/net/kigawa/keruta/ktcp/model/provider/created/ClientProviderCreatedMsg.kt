package net.kigawa.keruta.ktcp.model.provider.created

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.model.msg.client.ClientMsg
import net.kigawa.keruta.ktcp.model.msg.client.ClientMsgType

@Serializable
data class ClientProviderCreatedMsg(
    override val type: ClientMsgType = ClientMsgType.PROVIDER_CREATED,
    val provider: Provider,
): ClientMsg {
    init {
        require(type == ClientMsgType.PROVIDER_CREATED) { "type must be PROVIDER_CREATED" }
    }

    @Serializable
    data class Provider(
        val id: Long,
        val name: String,
        val issuer: String,
        val audience: String,
    )
}

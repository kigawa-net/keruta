package net.kigawa.keruta.ktcp.model.provider.deleted

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.model.msg.client.ClientMsg
import net.kigawa.keruta.ktcp.model.msg.client.ClientMsgType

@Serializable
data class ClientProviderDeletedMsg(
    override val type: ClientMsgType = ClientMsgType.PROVIDER_DELETED,
    val id: Long,
) : ClientMsg {
    init {
        require(type == ClientMsgType.PROVIDER_DELETED) { "type must be PROVIDER_DELETED" }
    }
}

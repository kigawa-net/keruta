package net.kigawa.keruta.ktcp.model.provider.add

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.model.msg.server.ServerMsg
import net.kigawa.keruta.ktcp.model.msg.server.ServerMsgType

@Serializable
data class ServerProviderIssueTokenMsg(
    override val type: ServerMsgType = ServerMsgType.PROVIDER_ISSUE_TOKEN,
    val issuer: String,
) : ServerMsg {
    init {
        require(type == ServerMsgType.PROVIDER_ISSUE_TOKEN) { "type must be PROVIDER_ADD" }
    }
}

package net.kigawa.keruta.ktcp.domain.provider.add

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.domain.msg.server.ServerMsg
import net.kigawa.keruta.ktcp.domain.msg.server.ServerMsgType

@Serializable
data class ServerProviderIssueTokenMsg(
    override val type: ServerMsgType = ServerMsgType.PROVIDER_ISSUE_TOKEN,
    val issuer: String,
) : ServerMsg {
    init {
        require(type == ServerMsgType.PROVIDER_ISSUE_TOKEN) { "type must be PROVIDER_ADD" }
    }
}

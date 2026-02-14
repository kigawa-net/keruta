package net.kigawa.keruta.ktcp.model.provider.issue_token

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.model.msg.server.ServerMsg
import net.kigawa.keruta.ktcp.model.msg.server.ServerMsgType

@Serializable
data class ServerProviderIssueTokenMsg(
    override val type: ServerMsgType = ServerMsgType.PROVIDER_ISSUE_TOKEN,
    val providerId: Long,
) : ServerMsg {
    init {
        require(type == ServerMsgType.PROVIDER_ISSUE_TOKEN) { "type must be PROVIDER_ISSUE_TOKEN" }
    }
}
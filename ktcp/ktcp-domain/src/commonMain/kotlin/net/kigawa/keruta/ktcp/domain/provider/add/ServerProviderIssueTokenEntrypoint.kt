package net.kigawa.keruta.ktcp.domain.provider.add

import net.kigawa.keruta.ktcp.domain.err.KtcpErr
import net.kigawa.keruta.ktcp.domain.msg.server.ServerMsgType
import net.kigawa.kodel.api.entrypoint.Entrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.kodel.api.err.Res

interface ServerProviderIssueTokenEntrypoint<C> : Entrypoint<ServerProviderIssueTokenMsg, EntrypointDeferred<Res<Unit, KtcpErr>>, C> {
    override val info: EntrypointInfo
        get() = EntrypointInfo(
            ServerMsgType.PROVIDER_ISSUE_TOKEN.str, emptyList(), "プロバイダートークン登録メッセージ処理"
        )
}

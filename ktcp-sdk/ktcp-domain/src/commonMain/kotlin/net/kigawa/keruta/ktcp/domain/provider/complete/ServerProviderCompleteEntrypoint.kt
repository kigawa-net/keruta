package net.kigawa.keruta.ktcp.domain.provider.complete

import net.kigawa.keruta.ktcp.domain.err.KtcpErr
import net.kigawa.keruta.ktcp.domain.msg.server.ServerMsgType
import net.kigawa.kodel.api.entrypoint.Entrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.kodel.api.err.Res

interface ServerProviderCompleteEntrypoint<C> : Entrypoint<ServerProviderCompleteMsg, EntrypointDeferred<Res<Unit, KtcpErr>>, C> {
    override val info: EntrypointInfo
        get() = EntrypointInfo(
            ServerMsgType.PROVIDER_COMPLETE.str, emptyList(), "プロバイダー完了メッセージ処理"
        )
}

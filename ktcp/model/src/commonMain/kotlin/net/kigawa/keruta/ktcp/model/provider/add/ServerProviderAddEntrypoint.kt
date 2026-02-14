package net.kigawa.keruta.ktcp.model.provider.add

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.msg.server.ServerMsgType
import net.kigawa.kodel.api.entrypoint.Entrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.kodel.api.err.Res

interface ServerProviderAddEntrypoint<C> : Entrypoint<ServerProviderAddMsg, EntrypointDeferred<Res<Unit, KtcpErr>>, C> {
    override val info: EntrypointInfo
        get() = EntrypointInfo(
            ServerMsgType.PROVIDER_ADD.str, emptyList(), "プロバイダー追加メッセージ処理"
        )
}

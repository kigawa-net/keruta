package net.kigawa.keruta.ktcp.model.provider.delete

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.msg.server.ServerMsgType
import net.kigawa.kodel.api.entrypoint.Entrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.kodel.api.err.Res

interface ServerProviderDeleteEntrypoint<C> : Entrypoint<ServerProviderDeleteMsg, EntrypointDeferred<Res<Unit, KtcpErr>>, C> {
    override val info: EntrypointInfo
        get() = EntrypointInfo(
            ServerMsgType.PROVIDER_DELETE.str, emptyList(), "プロバイダー削除メッセージ処理"
        )
}

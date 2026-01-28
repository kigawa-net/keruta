package net.kigawa.keruta.ktcp.model.provider.list

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.kodel.api.entrypoint.Entrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.kodel.api.err.Res

interface ServerProviderListEntrypoint<C>: Entrypoint<ServerProviderListArg, EntrypointDeferred<Res<Unit, KtcpErr>>, C> {
    override val info: EntrypointInfo
        get() = EntrypointInfo(
            "providers_request", emptyList(), "プロバイダー一覧要求メッセージ処理"
        )
}

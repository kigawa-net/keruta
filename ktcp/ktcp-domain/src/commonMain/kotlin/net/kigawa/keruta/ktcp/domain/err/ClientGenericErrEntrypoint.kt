package net.kigawa.keruta.ktcp.domain.err

import net.kigawa.kodel.api.entrypoint.Entrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.kodel.api.err.Res

interface ClientGenericErrEntrypoint<C>: Entrypoint<GenericErrMsg, EntrypointDeferred<Res<Unit, KtcpErr>>, C> {
    override val info: EntrypointInfo
        get() = EntrypointInfo("generic_error", emptyList(), "汎用エラーメッセージ処理")
}

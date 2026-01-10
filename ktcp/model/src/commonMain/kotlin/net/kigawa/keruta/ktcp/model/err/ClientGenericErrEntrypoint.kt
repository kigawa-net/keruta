package net.kigawa.keruta.ktcp.model.err

import net.kigawa.keruta.ktcp.model.err.server.types.KtcpServerErr
import net.kigawa.kodel.api.entrypoint.Entrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.kodel.api.err.Res

interface ClientGenericErrEntrypoint<C>: Entrypoint<GenericErrArg,EntrypointDeferred< Res<Unit, KtcpServerErr>>, C> {
    override val info: EntrypointInfo
        get() = EntrypointInfo("generic_error", emptyList(), "汎用エラーメッセージ処理")
}

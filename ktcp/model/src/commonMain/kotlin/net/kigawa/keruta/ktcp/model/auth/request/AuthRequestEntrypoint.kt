package net.kigawa.keruta.ktcp.model.auth.request

import net.kigawa.keruta.ktcp.model.err.server.types.KtcpServerErr
import net.kigawa.keruta.ktcp.model.err.server.types.VerifyErr
import net.kigawa.kodel.api.entrypoint.Entrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.kodel.api.err.Res

// エントリーポイントインターフェース定義
interface AuthRequestEntrypoint<C>: Entrypoint<AuthRequestArg, EntrypointDeferred< Res<Unit, KtcpServerErr>>, C> {
    override val info: EntrypointInfo
        get() = EntrypointInfo("authenticate", emptyList(), "認証メッセージ処理")
}

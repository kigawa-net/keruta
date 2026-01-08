package net.kigawa.keruta.ktcp.model.auth.request

import net.kigawa.keruta.ktcp.model.err.types.VerifyErr
import net.kigawa.kodel.api.entrypoint.Entrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.kodel.api.err.Res

// エントリーポイントインターフェース定義
interface AuthRequestEntrypoint<C>: Entrypoint<AuthRequestArg, Res<Unit, VerifyErr>, C> {
    override val info: EntrypointInfo
        get() = EntrypointInfo("authenticate", emptyList(), "認証メッセージ処理")
}

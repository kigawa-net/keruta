package net.kigawa.keruta.ktcp.model.authenticate

import net.kigawa.kodel.api.entrypoint.Entrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.kodel.api.err.Res

// エントリーポイントインターフェース定義
interface AuthenticateEntrypoint<C>: Entrypoint<AuthenticateArg, Res<Unit, Nothing>, C> {
    override val info: EntrypointInfo
        get() = EntrypointInfo("authenticate", emptyList(), "認証メッセージ処理")
}

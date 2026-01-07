package net.kigawa.keruta.ktcp.model.auth.sccess

import net.kigawa.kodel.api.entrypoint.Entrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.kodel.api.err.Res

// エントリーポイントインターフェース定義
interface AuthSuccessEntrypoint<C>: Entrypoint<AuthSuccessArg, EntrypointDeferred<in Res<Unit, Nothing>>, C> {
    override val info: EntrypointInfo
        get() = EntrypointInfo("authenticate", emptyList(), "認証メッセージ処理")
}

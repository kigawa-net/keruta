package net.kigawa.keruta.ktcp.model.auth.sccess

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.kodel.api.entrypoint.Entrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.kodel.api.err.Res

// エントリーポイントインターフェース定義
interface ClientAuthSuccessEntrypoint<C>: Entrypoint<ClientAuthSuccessMsg, EntrypointDeferred<Res<Unit, KtcpErr>>, C> {
    override val info: EntrypointInfo
        get() = EntrypointInfo("authenticate", emptyList(), "認証メッセージ処理")
}

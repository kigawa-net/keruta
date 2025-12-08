package net.kigawa.keruta.ktcp.model.authenticate

import net.kigawa.keruta.ktcp.model.KtcpRes
import net.kigawa.kodel.api.entrypoint.Entrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointInfo

// エントリーポイントインターフェース定義
interface AuthenticateEntrypoint: Entrypoint<AuthenticateMsg, KtcpRes> {
    override val info: EntrypointInfo
        get() = EntrypointInfo("authenticate", emptyList(), "認証メッセージ処理")

    override fun access(input: AuthenticateMsg): KtcpRes?
}

package net.kigawa.keruta.ktcp.model.entrypoint

import net.kigawa.keruta.ktcp.model.message.AuthenticateMessage
import net.kigawa.keruta.ktcp.model.message.KtcpMessage
import net.kigawa.kodel.api.entrypoint.Entrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointInfo

// エントリーポイントクラス定義
class AuthenticateEntrypoint : Entrypoint<AuthenticateMessage, KtcpMessage> {
    override val info = EntrypointInfo("authenticate", emptyList(), "認証メッセージ処理")

    override fun access(input: AuthenticateMessage): KtcpMessage {
        // 認証処理の実装（モデルではモック）
        // 実際の認証ロジックはプラットフォーム固有のレイヤーで実装
        return input // 認証成功の場合はそのまま返す、失敗の場合はErrorMessageを返す
    }
}

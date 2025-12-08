package net.kigawa.keruta.ktcp.server.authenticate

import net.kigawa.keruta.ktcp.model.KtcpRes
import net.kigawa.keruta.ktcp.model.authenticate.AuthenticateEntrypoint
import net.kigawa.keruta.ktcp.model.authenticate.AuthenticateMsg

import net.kigawa.keruta.ktcp.model.err.KtcpErrRes
import kotlin.time.ExperimentalTime

class ServerAuthenticateEntrypoint : AuthenticateEntrypoint {
    @OptIn(ExperimentalTime::class)
    override fun access(
        input: AuthenticateMsg,
    ): KtcpRes? {
        val request = input.tryToAuthenticate() as? AuthenticateRequest ?: return null
        // 基本的な認証チェック
        if (request.token.isBlank()) {
            KtcpErrRes(
                code = "AUTH_FAILED",
                message = "Token is required",
                retryable = false,
                timestamp = kotlin.time.Clock.System.now()
            )
        } else if (request.clientType != "provider") {
            KtcpErrRes(
                code = "AUTH_FAILED",
                message = "Invalid client type",
                retryable = false,
                timestamp = kotlin.time.Clock.System.now()
            )
        } else {
            // TODO: JWTトークンの検証を実装
            // 現在は基本チェックのみ
            null // 認証成功
        }
        return null
    }
}

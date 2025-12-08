package net.kigawa.keruta.ktcp.server.authenticate

import net.kigawa.keruta.ktcp.model.KtcpRes
import net.kigawa.keruta.ktcp.model.authenticate.AuthenticateEntrypoint
import net.kigawa.keruta.ktcp.model.authenticate.AuthenticateMsg
import net.kigawa.keruta.ktcp.model.authenticate.AuthenticateRequest
import net.kigawa.keruta.ktcp.model.err.KtcpErrRes
import kotlin.time.ExperimentalTime

class ServerAuthenticateEntrypoint : AuthenticateEntrypoint {
    @OptIn(ExperimentalTime::class)
    override fun access(
        input: AuthenticateMsg,
    ): KtcpRes? {
        return when (input) {
            is AuthenticateRequest -> {
                // 基本的な認証チェック
                if (input.token.isBlank()) {
                    KtcpErrRes(
                        code = "AUTH_FAILED",
                        message = "Token is required",
                        retryable = false,
                        timestamp = kotlin.time.Instant.now()
                    )
                } else if (input.clientType != "provider") {
                    KtcpErrRes(
                        code = "AUTH_FAILED",
                        message = "Invalid client type",
                        retryable = false,
                        timestamp = kotlin.time.Instant.now()
                    )
                } else {
                    // TODO: JWTトークンの検証を実装
                    // 現在は基本チェックのみ
                    null // 認証成功
                }
            }
        }
    }
}

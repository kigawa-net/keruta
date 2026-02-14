package net.kigawa.keruta.ktcl.claudecode.auth

import net.kigawa.keruta.ktcl.claudecode.config.ClaudeCodeConfig
import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.client.KtcpClient
import net.kigawa.keruta.ktcp.model.auth.request.ServerAuthRequestMsg
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.kodel.api.err.Res

class AuthManager(
    private val config: ClaudeCodeConfig,
    private val ktcpClient: KtcpClient,
    private val ctx: ClientCtx,
) {
    suspend fun authenticate(): Res<Unit, KtcpErr> {
        val authMsg = ServerAuthRequestMsg(
            userToken = config.userToken,
            serverToken = config.serverToken
        )

        return ktcpClient.ktcpServerEntrypoints.authRequestEntrypoint.access(
            authMsg,
            ctx
        )?.execute() ?: Res.Err(net.kigawa.keruta.ktcl.claudecode.err.ConnectionErr("Auth entrypoint not found", null))
    }
}
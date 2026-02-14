package net.kigawa.keruta.ktcl.k8s.auth

import net.kigawa.keruta.ktcl.k8s.config.K8sConfig
import net.kigawa.keruta.ktcl.k8s.err.K8sErr
import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.client.KtcpClient
import net.kigawa.keruta.ktcp.model.auth.request.ServerAuthRequestMsg
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.kodel.api.err.Res

class AuthManager(
    private val config: K8sConfig,
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
        )?.execute() ?: Res.Err(K8sErr.K8sClientErr("Auth entrypoint not found", null))
    }
}

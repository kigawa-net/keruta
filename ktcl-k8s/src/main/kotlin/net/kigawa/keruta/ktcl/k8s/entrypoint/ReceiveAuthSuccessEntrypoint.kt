package net.kigawa.keruta.ktcl.k8s.entrypoint

import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.model.auth.sccess.ClientAuthSuccessEntrypoint
import net.kigawa.keruta.ktcp.model.auth.sccess.ClientAuthSuccessMsg
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.log.LoggerFactory

class ReceiveAuthSuccessEntrypoint : ClientAuthSuccessEntrypoint<ClientCtx> {
    private val logger = LoggerFactory.get("ReceiveAuthSuccessEntrypoint")

    override fun access(
        input: ClientAuthSuccessMsg,
        ctx: ClientCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> = EntrypointDeferred {
        logger.info { "Authentication successful" }
        Res.Ok(Unit)
    }
}

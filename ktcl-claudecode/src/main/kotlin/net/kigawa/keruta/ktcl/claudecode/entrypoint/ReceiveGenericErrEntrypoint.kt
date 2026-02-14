package net.kigawa.keruta.ktcl.claudecode.entrypoint

import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.model.err.ClientGenericErrEntrypoint
import net.kigawa.keruta.ktcp.model.err.GenericErrMsg
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.log.LoggerFactory

class ReceiveGenericErrEntrypoint : ClientGenericErrEntrypoint<ClientCtx> {
    private val logger = LoggerFactory.get("ReceiveGenericErrEntrypoint")

    override fun access(
        input: GenericErrMsg,
        ctx: ClientCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> = EntrypointDeferred {
        logger.info { "Received error: $input" }
        Res.Ok(Unit)
    }
}

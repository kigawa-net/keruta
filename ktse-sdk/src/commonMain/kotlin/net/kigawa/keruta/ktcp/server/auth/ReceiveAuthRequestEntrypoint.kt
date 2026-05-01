package net.kigawa.keruta.ktcp.server.auth

import net.kigawa.keruta.ktcp.domain.auth.request.ServerAuthRequestEntrypoint
import net.kigawa.keruta.ktcp.domain.auth.request.ServerAuthRequestMsg
import net.kigawa.keruta.ktcp.domain.auth.sccess.ClientAuthSuccessMsg
import net.kigawa.keruta.ktcp.domain.err.KtcpErr
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.keruta.ktcp.server.err.UnexpectedErr
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.log.LoggerFactory
import net.kigawa.kodel.api.log.traceignore.debug

class ReceiveAuthRequestEntrypoint: ServerAuthRequestEntrypoint<ServerCtx> {
    val logger = LoggerFactory.get("net.kigawa.keruta.ktcp.server.authenticate.ReceiveAuthenticateEntrypoint")
    override fun access(
        input: ServerAuthRequestMsg, ctx: ServerCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> {
        logger.debug("accessing authenticate request")
        return EntrypointDeferred {
            when (val res = ctx.session.authenticate(input)) {
                is Res.Err -> res.convert<Unit>()
                    .also { logger.debug("failed to verify authenticate message") }

                is Res.Ok -> {

                    logger.debug("verified authenticate message")
                    ctx.server.clientEntrypoints.authSuccess.access(
                        ClientAuthSuccessMsg(), ctx
                    )?.execute() ?: Res.Err(UnexpectedErr("", null))
                }
            }
        }
    }

}

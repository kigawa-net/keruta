package net.kigawa.keruta.ktcp.server.auth

import net.kigawa.keruta.ktcp.model.auth.request.ServerAuthRequestArg
import net.kigawa.keruta.ktcp.model.auth.request.ServerAuthRequestEntrypoint
import net.kigawa.keruta.ktcp.model.auth.sccess.ClientAuthSuccessMsg
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.keruta.ktcp.server.auth.success.SendAuthAccessArg
import net.kigawa.keruta.ktcp.server.err.UnexpectedErr
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.log.LoggerFactory
import net.kigawa.kodel.api.log.traceignore.debug

class ReceiveAuthRequestEntrypoint: ServerAuthRequestEntrypoint<ServerCtx> {
    val logger = LoggerFactory.get("net.kigawa.keruta.ktcp.server.authenticate.ReceiveAuthenticateEntrypoint")
    override fun access(
        input: ServerAuthRequestArg, ctx: ServerCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> {
        logger.debug("accessing authenticate request")
        return EntrypointDeferred {
            when (val res = ctx.session.authenticate(input.authRequestMsg)) {
                is Res.Err -> res.convert<Unit>()
                    .also { logger.debug("failed to verify authenticate message") }

                is Res.Ok -> {

                    logger.debug("verified authenticate message")
                    ctx.server.clientEntrypoints.authSuccess.access(
                        SendAuthAccessArg(ClientAuthSuccessMsg()), ctx
                    )?.execute() ?: Res.Err(UnexpectedErr("", null))
                }
            }
        }
    }

}

package net.kigawa.keruta.ktcp.server.auth

import net.kigawa.keruta.ktcp.model.auth.request.AuthRequestArg
import net.kigawa.keruta.ktcp.model.auth.request.AuthRequestEntrypoint
import net.kigawa.keruta.ktcp.model.auth.sccess.AuthSuccessMsg
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.keruta.ktcp.server.auth.success.AuthAccessSendArg
import net.kigawa.keruta.ktcp.server.err.UnexpectedErr
import net.kigawa.keruta.ktcp.server.err.VerifyErr
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.log.LoggerFactory
import net.kigawa.kodel.api.log.traceignore.debug

class ReceiveAuthRequestEntrypoint: AuthRequestEntrypoint<ServerCtx> {
    val logger = LoggerFactory.get("net.kigawa.keruta.ktcp.server.authenticate.ReceiveAuthenticateEntrypoint")
    override fun access(
        input: AuthRequestArg, ctx: ServerCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> {
        logger.debug("accessing authenticate request")
        return EntrypointDeferred {
            when (val res = ctx.verify(input.authRequestMsg)) {
                is Res.Err<Verified, VerifyErr> -> res.convertType<Unit>()
                    .also { logger.debug("failed to verify authenticate message") }

                is Res.Ok<Verified, *> -> {
                    ctx.session.authenticate(res.value)
                    logger.debug("verified authenticate message")
                    ctx.server.clientEntrypoints.authSuccess.access(
                        AuthAccessSendArg(AuthSuccessMsg()), ctx
                    )?.execute() ?: Res.Err(UnexpectedErr("", null))
                }
            }
        }
    }

}

package net.kigawa.keruta.ktcp.server.auth

import net.kigawa.keruta.ktcp.model.auth.request.AuthRequestArg
import net.kigawa.keruta.ktcp.model.auth.request.AuthRequestEntrypoint
import net.kigawa.keruta.ktcp.model.err.types.VerifyErr
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.log.LoggerFactory
import net.kigawa.kodel.api.log.traceignore.debug

class ReceiveAuthRequestEntrypoint: AuthRequestEntrypoint<ServerCtx> {
    val logger = LoggerFactory.get("net.kigawa.keruta.ktcp.server.authenticate.ReceiveAuthenticateEntrypoint")
    override fun access(
        input: AuthRequestArg, ctx: ServerCtx,
    ): Res<Unit, VerifyErr> {
        return when (val res = ctx.verify(input.authRequestMsg)) {
            is Res.Err<Verified, VerifyErr> -> res.convertType<Unit>()
                .also { logger.debug("failed to verify authenticate message") }
            is Res.Ok<Verified, *> -> {
                ctx.session.authenticated(res.value)
                logger.debug("verified authenticate message")
                Res.Ok(Unit)
            }
        }
    }

}

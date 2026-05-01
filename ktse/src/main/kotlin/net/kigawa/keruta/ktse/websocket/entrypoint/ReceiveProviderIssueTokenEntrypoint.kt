package net.kigawa.keruta.ktse.websocket.entrypoint

import net.kigawa.keruta.ktcp.domain.err.KtcpErr
import net.kigawa.keruta.ktcp.domain.provider.add.ServerProviderIssueTokenEntrypoint
import net.kigawa.keruta.ktcp.domain.provider.add.ServerProviderIssueTokenMsg
import net.kigawa.keruta.ktcp.domain.provider.add_token.ClientProviderAddTokenMsg
import net.kigawa.keruta.ktcp.domain.serialize.serialize
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.keruta.ktcp.server.err.UnauthenticatedErr
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.log.getKogger
import net.kigawa.kodel.api.log.traceignore.debug

class ReceiveProviderIssueTokenEntrypoint: ServerProviderIssueTokenEntrypoint<ServerCtx> {
    private val logger = getKogger()
    override fun access(
        input: ServerProviderIssueTokenMsg, ctx: ServerCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> = EntrypointDeferred {
        val authed = ctx.session.authenticated()
            ?: return@EntrypointDeferred Res.Err(UnauthenticatedErr("", null))
        val token = when (val res = authed.createProviderRegisterToken()) {
            is Res.Err -> return@EntrypointDeferred res.convert()
            is Res.Ok -> res.value
        }

        logger.debug { "Created provider add token: $token" }

        ctx.connection.send(
            ctx.serializer.serialize(ClientProviderAddTokenMsg(token = token))
        )
        Res.Ok(Unit)
    }

}

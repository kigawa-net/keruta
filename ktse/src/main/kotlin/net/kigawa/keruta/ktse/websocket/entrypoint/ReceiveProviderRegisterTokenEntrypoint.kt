package net.kigawa.keruta.ktse.websocket.entrypoint

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.provider.add.ServerProviderAddMsg
import net.kigawa.keruta.ktcp.model.provider.add.ServerProviderRegisterTokenEntrypoint
import net.kigawa.keruta.ktcp.model.provider.add_token.ClientProviderAddTokenMsg
import net.kigawa.keruta.ktcp.model.serialize.serialize
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.keruta.ktcp.server.err.UnauthenticatedErr
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.log.getKogger
import net.kigawa.kodel.api.log.traceignore.debug

class ReceiveProviderRegisterTokenEntrypoint: ServerProviderRegisterTokenEntrypoint<ServerCtx> {
    private val logger = getKogger()
    override fun access(
        input: ServerProviderAddMsg, ctx: ServerCtx,
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

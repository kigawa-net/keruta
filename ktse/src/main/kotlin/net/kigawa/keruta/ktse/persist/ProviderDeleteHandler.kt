package net.kigawa.keruta.ktse.persist

import net.kigawa.keruta.ktcp.domain.err.KtcpErr
import net.kigawa.keruta.ktcp.domain.provider.delete.ServerProviderDeleteMsg
import net.kigawa.keruta.ktcp.domain.provider.deleted.ClientProviderDeletedMsg
import net.kigawa.keruta.ktcp.domain.serialize.serialize
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.keruta.ktcp.server.err.UnauthenticatedErr
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.log.getKogger
import net.kigawa.kodel.api.log.traceignore.debug

class ProviderDeleteHandler {
    private val logger = getKogger()

    suspend fun handle(msg: ServerProviderDeleteMsg, ctx: ServerCtx): Res<Unit, KtcpErr> {
        val authed = ctx.session.authenticated()
            ?: return Res.Err(UnauthenticatedErr("", null))
        val authedSession = authed.persisterSession as? ExposedAuthedPersisterSession
            ?: return Res.Err(UnauthenticatedErr("invalid session type", null))

        val result = authedSession.deleteProvider(msg.id)
        if (result is Res.Err) return result

        logger.debug { "Deleted provider: ${msg.id}" }

        ctx.connection.send(
            ctx.serializer.serialize(ClientProviderDeletedMsg(id = msg.id))
        )
        return Res.Ok(Unit)
    }
}

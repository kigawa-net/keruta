package net.kigawa.keruta.ktse.persist

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.provider.add.ServerProviderAddMsg
import net.kigawa.keruta.ktcp.model.provider.add_token.ClientProviderAddTokenMsg
import net.kigawa.keruta.ktcp.model.serialize.serialize
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.keruta.ktcp.server.err.UnauthenticatedErr
import net.kigawa.keruta.ktse.persist.db.DbPersister
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.log.getKogger
import net.kigawa.kodel.api.log.traceignore.debug

class ProviderAddHandler(
    val dbPersister: DbPersister,
) {
    private val logger = getKogger()

    suspend fun handle(msg: ServerProviderAddMsg, ctx: ServerCtx): Res<Unit, KtcpErr> {
        val authed = ctx.session.authenticated()
            ?: return Res.Err(UnauthenticatedErr("", null))
        val authedSession = authed.persisterSession as? ExposedAuthedPersisterSession
            ?: return Res.Err(UnauthenticatedErr("invalid session type", null))

        val token = dbPersister.execTransaction { dsl ->
            dsl.providerAddToken.insert(
                userId = authedSession.user.id,
                name = msg.name,
                issuer = msg.issuer,
                audience = msg.audience,
            )
        }
        logger.debug { "Created provider add token: $token" }

        ctx.connection.send(
            ctx.serializer.serialize(ClientProviderAddTokenMsg(token = token))
        )
        return Res.Ok(Unit)
    }
}

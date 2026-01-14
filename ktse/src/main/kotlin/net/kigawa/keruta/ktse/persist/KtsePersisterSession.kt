package net.kigawa.keruta.ktse.persist

import net.kigawa.keruta.ktcp.model.auth.request.ServerAuthRequestMsg
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.server.auth.JwtVerifier
import net.kigawa.keruta.ktcp.server.persist.AuthenticatedPersisterSession
import net.kigawa.keruta.ktcp.server.persist.PersisterSession
import net.kigawa.keruta.ktse.KtseConfig
import net.kigawa.keruta.ktse.db.DbPersister
import net.kigawa.kodel.api.err.Res

class KtsePersisterSession(
    dbPersister: DbPersister,
    jwtVerifier: JwtVerifier,
    ktseConfig: KtseConfig,
): PersisterSession {
    val userVerifier = UserVerifier(jwtVerifier, dbPersister, ktseConfig)
    val providerVerifier = ProviderVerifier(jwtVerifier, dbPersister, ktseConfig)

    override suspend fun verify(
        authRequestMsg: ServerAuthRequestMsg,
    ): Res<AuthenticatedPersisterSession, KtcpErr> {
        val user = when (
            val res = userVerifier.verifyStrToken(authRequestMsg.userToken)
        ) {
            is Res.Err -> return res.x()
            is Res.Ok -> res.value
        }
        val provider = when (
            val res = providerVerifier.verifyStrToken(
                authRequestMsg.serverToken,
                user
            )
        ) {
            is Res.Err -> return res.x()
            is Res.Ok -> res.value
        }
        return Res.Ok(KtseAuthenticatedPersisterSession(user, provider))
    }
}

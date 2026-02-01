package net.kigawa.keruta.ktse.persist

import net.kigawa.keruta.ktcp.model.auth.request.ServerAuthRequestMsg
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.server.auth.JwtVerifier
import net.kigawa.keruta.ktcp.server.persist.AuthenticatedPersisterSession
import net.kigawa.keruta.ktcp.server.persist.PersistedVerifyTables
import net.kigawa.keruta.ktcp.server.persist.PersisterSession
import net.kigawa.keruta.ktse.KtseConfig
import net.kigawa.keruta.ktse.auth.AuthTokenVerifier
import net.kigawa.keruta.ktse.auth.UnverifiedAuthTokens
import net.kigawa.keruta.ktse.persist.db.DbPersister
import net.kigawa.kodel.api.err.Res

class KtsePersisterSession(
    val dbPersister: DbPersister,
    val jwtVerifier: JwtVerifier,
    ktseConfig: KtseConfig,
): PersisterSession {
    val userVerifier = UserVerifier(jwtVerifier, dbPersister, ktseConfig)
    val providerVerifier = ProviderVerifier(jwtVerifier, dbPersister, ktseConfig)
    val authTokenVerifier = AuthTokenVerifier(jwtVerifier)

    override suspend fun auth(
        authRequestMsg: ServerAuthRequestMsg,
    ): Res<AuthenticatedPersisterSession, KtcpErr> {
        val unverifiedToken = when (
            val res = authTokenVerifier.decodeAuthRequestMsg(authRequestMsg)
        ) {
            is Res.Err -> return res.x()
            is Res.Ok -> res.value
        }
        return when (
            val res = getVerifyTables(unverifiedToken)
        ) {
            is Res.Err -> res.x()
            is Res.Ok -> verifyWithTable(unverifiedToken, res.value)
            null -> createUserAndVerify(unverifiedToken)
        }
    }

    private fun createUserAndVerify(
        unverifiedToken: UnverifiedAuthTokens,
    ): Res<AuthenticatedPersisterSession, KtcpErr> {
        TODO()
    }

    private fun verifyWithTable(
        unverifiedToken: UnverifiedAuthTokens, tables: PersistedVerifyTables,
    ): Res<AuthenticatedPersisterSession, KtcpErr> =
        when (val res = unverifiedToken.verify(tables)) {
            is Res.Err -> return res.x()
            is Res.Ok -> Res.Ok(ExposedAuthedPersisterSession(tables.user, tables.provider, dbPersister))
        }

    private fun getVerifyTables(
        unverifiedTokens: UnverifiedAuthTokens,
    ): Res<PersistedVerifyTables, KtcpErr>? = dbPersister.execTransaction {
        it.findVerifyTables(
            unverifiedTokens.userToken.issuer, unverifiedTokens.userToken.subject,
            unverifiedTokens.providerToken.issuer
        )

    }
}

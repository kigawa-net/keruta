package net.kigawa.keruta.ktse.persist

import net.kigawa.keruta.ktcp.model.auth.AuthToken
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.server.auth.JwtVerifier
import net.kigawa.keruta.ktcp.server.auth.UnverifiedToken
import net.kigawa.keruta.ktcp.server.persist.PersistedProvider
import net.kigawa.keruta.ktcp.server.persist.PersistedUser
import net.kigawa.keruta.ktcp.server.persist.PersistedUserIdp
import net.kigawa.keruta.ktse.KtseConfig
import net.kigawa.keruta.ktse.err.UnknownIssuerErr
import net.kigawa.keruta.ktse.persist.db.DbPersister
import net.kigawa.kodel.api.err.Res

class UserVerifier(
    val jwtVerifier: JwtVerifier,
    val dbPersister: DbPersister,
    val ktcpConfig: KtseConfig,
) {
    suspend fun verifyStrToken(userToken: AuthToken, provider: PersistedProvider): Res<PersistedUser, KtcpErr> = when (
        val res = jwtVerifier.decodeUnverified(userToken)
    ) {
        is Res.Err -> res.x()
        is Res.Ok -> verifyToken(res.value, provider)
    }

    private suspend fun verifyToken(
        unverifiedToken: UnverifiedToken, provider: PersistedProvider,
    ): Res<PersistedUser, KtcpErr> = when (
        val res = dbPersister.execTransaction {
            it.user.getUserIdpOrNull(
                unverifiedToken.subject, unverifiedToken.issuer
            )
        }
    ) {
        is Res.Err -> res.x()
        is Res.Ok -> verifyWithUserIdp(unverifiedToken, res.value)
        null -> createUser(unverifiedToken, provider)
    }

    private suspend fun createUser(
        unverifiedToken: UnverifiedToken, provider: PersistedProvider,
    ): Res<PersistedUser, KtcpErr> {
        val idp = ktcpConfig.defaultIdp.firstOrNull { it.issuer == unverifiedToken.issuer }
        if (idp == null) return Res.Err(UnknownIssuerErr("", null))
        return when (
            val res = unverifiedToken.verify(
                idp.asIdp(unverifiedToken.subject), true
            )
        ) {
            is Res.Err -> res.x()
            is Res.Ok -> dbPersister.execTransaction {
                it.user.createUserAndIdp(idp, res.value, provider)
            }
        }
    }

    suspend fun verifyWithUserIdp(
        unverifiedToken: UnverifiedToken, userIdp: PersistedUserIdp,
    ): Res<PersistedUser, KtcpErr> = when (
        val res = unverifiedToken.verify(userIdp.asUserIdp(), true)
    ) {
        is Res.Err -> res.x()
        is Res.Ok -> dbPersister.execTransaction { it.user.getUser(userIdp) }
    }
}

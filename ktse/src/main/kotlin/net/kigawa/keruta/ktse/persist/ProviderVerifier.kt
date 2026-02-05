package net.kigawa.keruta.ktse.persist

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.server.auth.JwtVerifier
import net.kigawa.keruta.ktcp.server.auth.jwt.UnverifiedToken
import net.kigawa.keruta.ktcp.server.persist.PersistedProvider
import net.kigawa.keruta.ktcp.server.persist.PersistedUser
import net.kigawa.keruta.ktse.KtseConfig
import net.kigawa.keruta.ktse.err.UnknownIssuerErr
import net.kigawa.keruta.ktse.persist.db.DbPersister
import net.kigawa.kodel.api.err.Res

class ProviderVerifier(
    val jwtVerifier: JwtVerifier,
    val dbPersister: DbPersister,
    val ktcpConfig: KtseConfig,
) {
//    suspend fun verifyStrToken(serverToken: String, user: PersistedUser): Res<PersistedProvider, KtcpErr> =
//        when (
//            val res = jwtVerifier.decodeUnverified(serverToken)
//        ) {
//            is Res.Err -> res.x()
//            is Res.Ok -> verifyToken(res.value, user)
//        }

//    private suspend fun verifyToken(
//        unverifiedToken: UnverifiedToken, user: PersistedUser,
//    ): Res<PersistedProvider, KtcpErr> = when (
//        val res = dbPersister.execTransaction {
//            it.provider.findByIssuerAndUser(
//                unverifiedToken.issuer, user
//            )
//        }
//    ) {
//        is Res.Err -> res.x()
//        is Res.Ok -> verifyProvider(unverifiedToken, res.value, user)
//        null -> newProvider(unverifiedToken, user)
//    }

//    private suspend fun newProvider(
//        unverifiedToken: UnverifiedToken,
//        user: PersistedUser,
//    ): Res<PersistedProvider, KtcpErr> {
//        val idp = ktcpConfig.defaultProvider
//            .firstOrNull { it.issuer == unverifiedToken.issuer }
//        if (idp == null) return Res.Err(UnknownIssuerErr("token: $unverifiedToken", null))
//        return when (
//            val res = unverifiedToken.verify(
//                idp.asIdp(user.currentIdp.subject), false
//            )
//        ) {
//            is Res.Err -> res.x()
//            is Res.Ok -> dbPersister.execTransaction {
//                it.provider.createProvider(idp, user)
//            }
//        }
//    }

//    private suspend fun verifyProvider(
//        unverifiedToken: UnverifiedToken, provider: PersistedProvider, user: PersistedUser,
//    ): Res<PersistedProvider, KtcpErr> = when (
//        val res = unverifiedToken.verify(
//            provider.asUserIdp(user.currentIdp.subject), false
//        )
//    ) {
//        is Res.Err -> res.x()
//        is Res.Ok -> Res.Ok(provider)
//    }
}

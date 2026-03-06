package net.kigawa.keruta.ktcp.server.session

import net.kigawa.keruta.ktcp.model.auth.jwt.JwtVerifyValues
import net.kigawa.keruta.ktcp.model.auth.request.ServerAuthRequestMsg
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.server.auth.ProviderIdpConfig
import net.kigawa.keruta.ktcp.server.auth.UnverifiedAuthTokens
import net.kigawa.keruta.ktcp.server.auth.UserIdpConfig
import net.kigawa.keruta.ktcp.server.auth.VerifyTablesPersister
import net.kigawa.keruta.ktcp.server.auth.jwt.AuthTokenDecoder
import net.kigawa.keruta.ktcp.server.persist.PersistedVerifyTables
import net.kigawa.keruta.ktcp.server.persist.PersisterSession
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.err.flatConvertOk

class SessionAuthenticator(
    val session: KtcpSession, val userIdpConfig: UserIdpConfig,
    val providerIdpConfig: ProviderIdpConfig,
) {
    val persisterSession: PersisterSession by session::persisterSession
    val authTokenDecoder: AuthTokenDecoder by session::authTokenDecoder
    val verifyTablesPersister: VerifyTablesPersister by session::verifyTablesPersister

    suspend fun authenticate(authRequestMsg: ServerAuthRequestMsg): Res<AuthenticatedSession, KtcpErr> {
        val verifyTables = when (
            val res = auth(
                authRequestMsg
            )
        ) {
            is Res.Err -> return res.convert()
            is Res.Ok -> res.value
        }
        return Res.Ok(
            AuthenticatedSession(
                persisterSession.auth(verifyTables),
                session
            )
        )
    }

    private suspend fun auth(
        authRequestMsg: ServerAuthRequestMsg,
    ): Res<PersistedVerifyTables, KtcpErr> {
        val unverifiedTokens = when (
            val res = authTokenDecoder.decodeAuthRequestMsg(authRequestMsg)
        ) {
            is Res.Err -> return res.convert()
            is Res.Ok -> res.value
        }
        return when (
            val res = verifyTablesPersister.getVerifyTables(
                unverifiedTokens
            )
        ) {
            is Res.Err -> res.convert()
            is Res.Ok -> verifyWithTable(unverifiedTokens, res.value)
            null -> createUserAndVerify(unverifiedTokens)
        }
    }

    private suspend fun createUserAndVerify(
        unverifiedToken: UnverifiedAuthTokens,
    ): Res<PersistedVerifyTables, KtcpErr> = unverifiedToken.verify(
        JwtVerifyValues(userIdpConfig.issuer, userIdpConfig.audience, unverifiedToken.subject),
        JwtVerifyValues(
            providerIdpConfig.issuer, providerIdpConfig.audience, unverifiedToken.subject
        ),
    ).flatConvertOk {
        persisterSession.verifyTablesPersister.createVerifyTables(it, providerIdpConfig.name)
    }


    private suspend fun verifyWithTable(
        unverifiedToken: UnverifiedAuthTokens, tables: PersistedVerifyTables,
    ): Res<PersistedVerifyTables, KtcpErr> = when (
        val res = unverifiedToken.verify(
            JwtVerifyValues(tables.userIdp.issuer, tables.userIdp.audience, tables.userIdp.subject),
            JwtVerifyValues(tables.provider.issuer, tables.provider.audience, tables.userIdp.subject)
        )
    ) {
        is Res.Err -> res.convert()
        is Res.Ok -> Res.Ok(tables)
    }

}

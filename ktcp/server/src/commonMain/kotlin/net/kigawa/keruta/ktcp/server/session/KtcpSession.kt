package net.kigawa.keruta.ktcp.server.session

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import net.kigawa.keruta.ktcp.model.auth.jwt.JwtVerifyValues
import net.kigawa.keruta.ktcp.model.auth.key.PemKey
import net.kigawa.keruta.ktcp.model.auth.request.ServerAuthRequestMsg
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.provider.complete.ServerProviderCompleteMsg
import net.kigawa.keruta.ktcp.model.provider.idp_added.ClientProviderIdpAddedMsg
import net.kigawa.keruta.ktcp.model.serialize.serialize
import net.kigawa.keruta.ktcp.server.KtcpConnection
import net.kigawa.keruta.ktcp.server.KtcpServer
import net.kigawa.keruta.ktcp.server.auth.ProviderIdpConfig
import net.kigawa.keruta.ktcp.server.auth.UserIdpConfig
import net.kigawa.keruta.ktcp.server.auth.VerifyTablesPersister
import net.kigawa.keruta.ktcp.server.auth.jwt.AuthTokenDecoder
import net.kigawa.keruta.ktcp.server.persist.PersisterSession
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.err.flatConvertOk
import net.kigawa.kodel.api.err.unwrap
import net.kigawa.kodel.coroutine.CounterInDuration
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class KtcpSession private constructor(
    val connection: KtcpConnection,
    val persisterSession: PersisterSession,
    val authTokenDecoder: AuthTokenDecoder,
    val verifyTablesPersister: VerifyTablesPersister,
    userIdpConfig: UserIdpConfig,
    providerIdpConfig: ProviderIdpConfig,
    private val pemKey: PemKey,
) {
    val server: KtcpServer by connection::server
    private val counterInDuration = CounterInDuration(30.minutes, 3)
    private val lastAccess = MutableStateFlow(Clock.System.now())
    private val isClosed = MutableStateFlow(false)
    private val authenticatedSession = MutableStateFlow<AuthenticatedSession?>(null)
    private val sessionAuthenticator = SessionAuthenticator(
        this, userIdpConfig, providerIdpConfig
    )

    companion object {
        suspend fun startSession(
            connection: KtcpConnection, persisterSession: PersisterSession, authTokenDecoder: AuthTokenDecoder,
            verifyTablesPersister: VerifyTablesPersister, userIdpConfig: UserIdpConfig,
            providerIdpConfig: ProviderIdpConfig, pemKey: PemKey,
            block: suspend (KtcpSession) -> Unit,
        ) {
            KtcpSession(
                connection, persisterSession, authTokenDecoder, verifyTablesPersister, userIdpConfig,
                providerIdpConfig, pemKey
            ).also {
                coroutineScope {
//                    launch {
//                        while (!it.isClosed.value) {
//                            val current = Clock.System.now()
//                            val last = it.lastAccess.value
//                            val limit = last + it.timeout
//                            if (limit <= current) {
//
//                                it.close()
//                            }
//                            delay(limit - current)
//                        }
//                    }
                    launch { block(it) }
                }
            }
        }
    }

    suspend fun authenticate(authRequestMsg: ServerAuthRequestMsg): Res<AuthenticatedSession, KtcpErr> = when (
        val res = sessionAuthenticator.authenticate(
            authRequestMsg
        )
    ) {
        is Res.Err -> res
        is Res.Ok -> {
            authenticatedSession.value = res.value
            res
        }
    }

    fun authenticated() = authenticatedSession.value

    fun updateTimeout() {
        lastAccess.value = Clock.System.now()
    }

    suspend fun recordErr() {
        if (!counterInDuration.addAndCheckOverflow()) return
        close()
    }

    fun close() {
        isClosed.value = true
    }

    suspend fun registerProvider(input: ServerProviderCompleteMsg): Res<Unit, KtcpErr> {
        val unverifiedRegisterToken = server.jwtVerifier.decodeUnverified(input.registerToken).flatConvertOk {
            it.withKey(pemKey)
        }.unwrap {
            return Res.Err(it)
        }
        val userTables = persisterSession.verifyTablesPersister.getUserTables(
            unverifiedRegisterToken.unverifiedToken.issuer,
            unverifiedRegisterToken.unverifiedToken.subject
        ).unwrap {
            return Res.Err(it)
        }
        val userToken = unverifiedRegisterToken.verify(
            JwtVerifyValues(
                userTables.second.issuer,
                "provider_register",
                userTables.second.subject
            )
        ).flatConvertOk {
            server.jwtVerifier.decodeUnverified(input.userToken).flatConvertOk {
                it.verifyWithOidcJwks(
                    JwtVerifyValues(
                        it.issuer, input.userAudience, it.subject
                    )
                )
            }
        }.unwrap {
            return Res.Err(it)
        }

        val providerToken = server.jwtVerifier.decodeUnverified(input.serverToken).flatConvertOk {
            it.verifyWithJwks(
                JwtVerifyValues(
                    it.issuer, input.providerAudience, userToken.subject
                )
            )
        }.unwrap {
            return Res.Err(it)
        }
        persisterSession.verifyTablesPersister.saveProviderForUser(
            userTables.first, providerToken,input.providerAudience,
            input.userAudience,input.providerName, userToken.issuer
        )


        connection.send(
            server.serializer.serialize(ClientProviderIdpAddedMsg())
        )
        return Res.Ok(Unit)
    }
}

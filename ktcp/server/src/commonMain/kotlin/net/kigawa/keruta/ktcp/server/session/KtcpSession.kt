package net.kigawa.keruta.ktcp.server.session

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import net.kigawa.keruta.ktcp.model.auth.request.ServerAuthRequestMsg
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.server.KtcpConnection
import net.kigawa.keruta.ktcp.server.KtcpServer
import net.kigawa.keruta.ktcp.server.auth.ProviderIdpConfig
import net.kigawa.keruta.ktcp.server.auth.UserIdpConfig
import net.kigawa.keruta.ktcp.server.auth.VerifyTablesPersister
import net.kigawa.keruta.ktcp.server.auth.jwt.AuthTokenDecoder
import net.kigawa.keruta.ktcp.server.persist.PersisterSession
import net.kigawa.kodel.api.err.Res
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
) {
    val server: KtcpServer by connection::server
    private val counterInDuration = CounterInDuration(30.minutes, 3)
    private val lastAccess = MutableStateFlow(Clock.System.now())
    private val timeout = 1.minutes
    private val isClosed = MutableStateFlow(false)
    private val authenticatedSession = MutableStateFlow<AuthenticatedSession?>(null)
    private val sessionAuthenticator = SessionAuthenticator(
        this, userIdpConfig, providerIdpConfig
    )

    companion object {
        suspend fun startSession(
            connection: KtcpConnection, persisterSession: PersisterSession, authTokenDecoder: AuthTokenDecoder,
            verifyTablesPersister: VerifyTablesPersister, userIdpConfig: UserIdpConfig,
            providerIdpConfig: ProviderIdpConfig,
            block: suspend (KtcpSession) -> Unit,
        ) {
            KtcpSession(
                connection, persisterSession, authTokenDecoder, verifyTablesPersister, userIdpConfig, providerIdpConfig
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
}

package net.kigawa.keruta.ktcp.server.session

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import net.kigawa.keruta.ktcp.model.KtcpConnection
import net.kigawa.keruta.ktcp.server.persist.PersisterSession
import net.kigawa.kodel.coroutine.CounterInDuration
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class KtcpSession private constructor(
    val connection: KtcpConnection,
    val persisterSession: PersisterSession,
) {
    private val counterInDuration = CounterInDuration(30.minutes, 3)
    private val lastAccess = MutableStateFlow(Clock.System.now())
    private val timeout = 1.minutes
    private val isClosed = MutableStateFlow(false)
    private val authenticatedSession = MutableStateFlow<AuthenticatedSession?>(null)


    companion object {
        suspend fun startSession(
            connection: KtcpConnection, persisterSession: PersisterSession, block: suspend (KtcpSession) -> Unit,
        ) {
            KtcpSession(connection, persisterSession).also {
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

    fun authenticate(value: AuthenticatedSession) {
        authenticatedSession.value = value
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

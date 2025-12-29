package net.kigawa.keruta.ktcp.server

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import net.kigawa.kodel.coroutine.CounterInDuration
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class KtcpSession private constructor(
    val connection: KtcpConnection,
) {
    private val counterInDuration = CounterInDuration(30.minutes, 3)
    private val lastAccess = MutableStateFlow(Clock.System.now())
    private val timeout = 1.minutes
    private val isAuthenticated = MutableStateFlow(false)
    private val isClosed = MutableStateFlow(false)

    companion object {
        suspend fun startSession(connection: KtcpConnection, block: suspend (KtcpSession) -> Unit) {
            KtcpSession(connection).also {
                coroutineScope {
                    launch {
                        while (!it.isClosed.value) {
                            val current = Clock.System.now()
                            val last = it.lastAccess.value
                            val limit = last + it.timeout
                            if (limit <= current) {

                                it.close()
                            }
                            delay(limit - current)
                        }
                    }
                }
                block(it)
            }
        }
    }

    fun authenticated() {
        isAuthenticated.value = true
    }

    suspend fun updateTimeout() {
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

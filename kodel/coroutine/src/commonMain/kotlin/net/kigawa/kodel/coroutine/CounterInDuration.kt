package net.kigawa.kodel.coroutine

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class CounterInDuration(
    private val timeout: Duration,
    private val threshold: Int,
) {
    private val list = LinkedList<Instant>()
    private val mutex = Mutex()
    suspend fun addAndCheckOverflow(): Boolean {
        val now = Clock.System.now()
        return mutex.withLock {
            val limit = now - timeout
            list.removeIf { it < limit }
            list.add(now)
            list.size >= threshold
        }
    }

}

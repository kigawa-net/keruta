package net.kigawa.kodel.coroutine.cache

import kotlin.time.Clock
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class TtlCacheEntry<T>(val ttl: Long, private val value: T) {
    var timeout = Clock.System.now() + ttl.toDuration(DurationUnit.MILLISECONDS)
        private set

    fun get(): T {
        timeout = Clock.System.now() + ttl.toDuration(DurationUnit.MILLISECONDS)
        return value
    }
}

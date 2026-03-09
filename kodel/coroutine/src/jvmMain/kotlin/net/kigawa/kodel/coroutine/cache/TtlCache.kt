package net.kigawa.kodel.coroutine.cache

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Clock

class TtlCache<K, V>(
    val ttl: Long,
) {
    private val map = TtlCacheMap<K, V>()
    private val mutex = Mutex()
    suspend operator fun get(key: K): V? = mutex.withLock { map[key]?.get() }
    suspend operator fun set(key: K, value: V) {
        mutex.withLock { map[key] = TtlCacheEntry(ttl, value) }
    }

    suspend fun getOrPut(key: K, block: suspend () -> V): V = mutex.withLock {
        map.getOrPut(key) { TtlCacheEntry(ttl, block()) }.get()
    }

    class TtlCacheMap<K, V>: LinkedHashMap<K, TtlCacheEntry<V>>(
        16, 0.75f, true
    ) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, TtlCacheEntry<V>>): Boolean {
            return eldest.value.timeout < Clock.System.now()
        }
    }
}

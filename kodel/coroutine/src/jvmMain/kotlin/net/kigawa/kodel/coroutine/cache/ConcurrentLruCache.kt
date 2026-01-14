package net.kigawa.kodel.coroutine.cache

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.kigawa.kodel.api.cache.LruCache

class ConcurrentLruCache<K, V>(size: Int) {
    private val cache = LruCache<K, V>(size)
    val mutex = Mutex()
    suspend fun <R> use(block: suspend LruCache<K, V>.() -> R): R = mutex.withLock {
        cache.block()
    }
}

package net.kigawa.kodel.api.cache

class LruCache<K, V>(
    private val maxSize: Int,
): LinkedHashMap<K, V>(16, 0.75f, true) {

    override fun removeEldestEntry(
        eldest: MutableMap.MutableEntry<K, V>,
    ): Boolean {
        return size > maxSize
    }
}

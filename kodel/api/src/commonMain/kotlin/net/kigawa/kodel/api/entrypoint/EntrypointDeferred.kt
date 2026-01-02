package net.kigawa.kodel.api.entrypoint

class EntrypointDeferred<R>(
    val block: suspend () -> R,
) {
    suspend fun execute(): R = block()
}

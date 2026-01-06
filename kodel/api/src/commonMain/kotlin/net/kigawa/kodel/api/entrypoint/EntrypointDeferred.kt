package net.kigawa.kodel.api.entrypoint

class EntrypointDeferred<R>(
    val block: suspend () -> R,
) {
    suspend fun execute(): R = block()

    @Suppress("unused")
    fun <S> map(function: (R) -> S): EntrypointDeferred<S> = EntrypointDeferred { function(execute()) }
}

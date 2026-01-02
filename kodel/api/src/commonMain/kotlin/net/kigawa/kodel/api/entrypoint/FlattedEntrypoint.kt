package net.kigawa.kodel.api.entrypoint

open class FlattedEntrypoint<in I, out O, C>(
    val path: List<EntrypointInfo>,
    val entrypoint: EntrypointNode<I, O, C>,
): EntrypointNode<I, O, C> {
    override suspend fun access(input: I, ctx: C): O? {
        return entrypoint.access(input, ctx)
    }

    override fun flat(): List<FlattedEntrypoint<I, O, C>> {
        return listOf(this)
    }
}

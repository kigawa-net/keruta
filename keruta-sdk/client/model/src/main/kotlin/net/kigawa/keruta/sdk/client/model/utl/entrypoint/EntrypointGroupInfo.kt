package net.kigawa.keruta.sdk.client.model.utl.entrypoint

interface EntrypointGroupInfo<in IA, out IR, in OA, out OR>: SubEntryComponent<IA, IR> {
    val subComponents: List<SubEntryComponent<IA, IR>>
    override fun <A, R> translate(
        block: ((IA) -> IR).(A) -> R,
    ): SubEntryComponent<A, R> {
        return TranslatedEntrypointGroupInfo(block, this)
    }
}

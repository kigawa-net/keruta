package net.kigawa.keruta.sdk.client.model.utl.entrypoint

class TranslatedEntrypointGroupInfo<IA, IR, OA, OR>(
    val block: ((OA) -> OR).(IA) -> IR,
    val source: EntrypointGroupInfo<OA, OR, *, *>,
): EntrypointGroupInfo<IA, IR, OA, OR> {
    override val subComponents: List<SubEntryComponent<IA, IR>>
        get() = source.subComponents.map {
            it.translate(block)
        }

}

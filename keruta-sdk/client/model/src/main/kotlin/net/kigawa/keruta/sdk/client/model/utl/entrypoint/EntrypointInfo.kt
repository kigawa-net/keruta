package net.kigawa.keruta.sdk.client.model.utl.entrypoint

class EntrypointInfo<in A, out R>(
    val name: String,
    val block: (A) -> R,
): SubEntryComponent<A, R> {
    override fun <IA, IR> translate(block: ((A) -> R).(IA) -> IR): EntrypointInfo<IA, IR> {
        return EntrypointInfo(name = name) { this.block.block(it) }
    }
}

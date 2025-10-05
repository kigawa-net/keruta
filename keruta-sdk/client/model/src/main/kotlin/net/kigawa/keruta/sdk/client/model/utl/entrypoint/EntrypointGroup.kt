package net.kigawa.keruta.sdk.client.model.utl.entrypoint

abstract class EntrypointGroup<A, R> {

    val info: NormalEntrypointGroupInfo<A, R> = NormalEntrypointGroupInfo()

    fun <G: EntrypointGroup<A, R>> group(group: G): G {
        return group.also { info.subComponents.add(it.info) }
    }

    fun <IA, IR, G: EntrypointGroup<IA, IR>> group(group: G, block: ((IA) -> IR).(A) -> R): G {
        return group.also { info.subComponents.add(it.info.translate(block)) }
    }

    fun entrypoint(name: String, block: (A) -> R): EntrypointInfo<A, R> {
        return EntrypointInfo(name, block).also { info.subComponents.add(it) }
    }
}

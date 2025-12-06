package net.kigawa.keruta.sdk.client.model.utl.entrypoint

class NormalEntrypointGroupInfo<A, R>: EntrypointGroupInfo<A, R, A, R> {
    override val subComponents: MutableList<SubEntryComponent<A, R>> = mutableListOf()

}

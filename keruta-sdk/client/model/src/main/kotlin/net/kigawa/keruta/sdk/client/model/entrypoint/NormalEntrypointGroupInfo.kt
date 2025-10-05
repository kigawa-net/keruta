package net.kigawa.keruta.sdk.client.model.entrypoint

class NormalEntrypointGroupInfo<A, R>: EntrypointGroupInfo<A, R, A, R> {
    override val subComponents: MutableList<SubEntryComponent<A, R>> = mutableListOf()

}

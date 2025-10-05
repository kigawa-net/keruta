package net.kigawa.keruta.sdk.client.model.entrypoint

interface SubEntryComponent<in A, out R> {
    fun <IA, IR> translate(block: ((A) -> R).(IA) -> IR): SubEntryComponent<IA, IR>
}

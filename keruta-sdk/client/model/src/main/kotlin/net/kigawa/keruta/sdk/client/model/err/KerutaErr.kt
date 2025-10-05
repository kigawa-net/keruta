package net.kigawa.keruta.sdk.client.model.err

abstract class KerutaErr(
    cause: Throwable? = null,
    suppressed: List<Throwable>? = null,
): Exception("", cause) {
    init {
        suppressed?.forEach(::addSuppressed)
    }

    override val message: String
        get() = toString()
}

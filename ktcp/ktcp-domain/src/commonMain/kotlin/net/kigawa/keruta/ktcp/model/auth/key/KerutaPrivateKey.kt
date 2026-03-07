package net.kigawa.keruta.ktcp.model.auth.key

@Deprecated("Use PemKey")
data class KerutaPrivateKey(
    val strKey: PemKey,
)

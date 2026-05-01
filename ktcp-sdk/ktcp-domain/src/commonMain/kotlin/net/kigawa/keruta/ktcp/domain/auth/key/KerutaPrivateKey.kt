package net.kigawa.keruta.ktcp.domain.auth.key

@Deprecated("Use PemKey")
data class KerutaPrivateKey(
    val strKey: PemKey,
)

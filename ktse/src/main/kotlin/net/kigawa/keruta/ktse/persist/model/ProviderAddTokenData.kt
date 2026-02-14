package net.kigawa.keruta.ktse.persist.model

data class ProviderAddTokenData(
    val userId: Long,
    val name: String,
    val issuer: String,
    val audience: String,
)

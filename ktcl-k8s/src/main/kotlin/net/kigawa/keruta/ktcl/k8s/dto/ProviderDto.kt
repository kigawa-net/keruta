package net.kigawa.keruta.ktcl.k8s.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProviderDto(
    val id: Long,
    val name: String,
    val issuer: String,
    val audience: String,
)

@Serializable
data class ProvidersResponse(
    val providers: List<ProviderDto>,
)

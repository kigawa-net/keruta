package net.kigawa.keruta.ktcp.domain.client.wellknown

import kotlinx.serialization.Serializable

@Serializable
data class KerutaWellKnownJson(
    val version: String,
    val issuer: String,
    val login: String,
    val queueProperties: ObjectPropertyJson,
)

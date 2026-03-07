package net.kigawa.keruta.ktcp.domain.client.wellknown

import kotlinx.serialization.Serializable

@Serializable
data class ArrayPropertyJson(
    val value: KtclPropertyJson,
): KtclPropertyJson

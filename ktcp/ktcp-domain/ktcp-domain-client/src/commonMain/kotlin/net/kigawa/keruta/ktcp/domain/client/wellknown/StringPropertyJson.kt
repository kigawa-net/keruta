package net.kigawa.keruta.ktcp.domain.client.wellknown

import kotlinx.serialization.Serializable

@Serializable
class StringPropertyJson(
    val nullable: Boolean,
): KtclPropertyJson

package net.kigawa.keruta.ktcp.domain.client.wellknown

import kotlinx.serialization.Serializable

@Serializable
data class ObjectPropertyJson(
    val fields: List<Field>,
): KtclPropertyJson {
    @Serializable
    data class Field(
        val fieldId: String,
        val fieldName: String,
        val value: KtclPropertyJson,
    )
}

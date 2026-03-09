package net.kigawa.keruta.ktcp.domain.serialize

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import net.kigawa.keruta.ktcp.domain.err.KtcpErr
import net.kigawa.kodel.api.err.Res

interface KerutaSerializer {
    val serializersModule: SerializersModule
    fun <T> serialize(serializer: SerializationStrategy<T>, value: T): String
    fun <T> deserialize(deserializer: DeserializationStrategy<T>, str: String): Res<T, KtcpErr>
}

inline fun <reified T> KerutaSerializer.deserialize(str: String): Res<T, KtcpErr> = deserialize(
    serializersModule.serializer(), str
)

inline fun <reified T> KerutaSerializer.serialize(msg: @Serializable T): String = serialize(
    serializersModule.serializer(), msg
)

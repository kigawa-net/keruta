package net.kigawa.keruta.ktcp.model.serialize

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import net.kigawa.kodel.api.err.Res

interface MsgSerializer {
    val serializersModule: SerializersModule
    fun serialize(msg: @Serializable Any): String

    fun <T> deserialize(deserializer: DeserializationStrategy<T>, str: String): Res<T, DeserializeErr>
}

inline fun <reified T> MsgSerializer.deserialize(str: String): Res<T, DeserializeErr> = deserialize(
    serializersModule.serializer(), str
)

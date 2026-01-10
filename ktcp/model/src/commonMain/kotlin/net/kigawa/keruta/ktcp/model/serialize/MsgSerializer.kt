package net.kigawa.keruta.ktcp.model.serialize

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import net.kigawa.keruta.ktcp.model.err.server.types.DeserializeErr
import net.kigawa.kodel.api.err.Res

interface MsgSerializer {
    val serializersModule: SerializersModule
    fun <T> serialize(serializer: SerializationStrategy<T>, value: T): String
    fun <T> deserialize(deserializer: DeserializationStrategy<T>, str: String): Res<T, DeserializeErr>
}

inline fun <reified T> MsgSerializer.deserialize(str: String): Res<T, DeserializeErr> = deserialize(
    serializersModule.serializer(), str
)

inline fun <reified T> MsgSerializer.serialize(msg: @Serializable T): String = serialize(
    serializersModule.serializer(),msg
)

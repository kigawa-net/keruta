package net.kigawa.keruta.ktcp.usecase

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationException
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import net.kigawa.keruta.ktcp.model.err.IllegalFormatDeserializeErr
import net.kigawa.keruta.ktcp.model.err.InvalidTypeDeserializeErr
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.serialize.KerutaSerializer
import net.kigawa.kodel.api.err.Res

class JsonKerutaSerializer: KerutaSerializer {
    val json = Json { encodeDefaults = true }
    override val serializersModule: SerializersModule
        get() = json.serializersModule

    override fun <T> serialize(serializer: SerializationStrategy<T>, value: T): String {
        return json.encodeToString(serializer, value)
    }


    override fun <T> deserialize(deserializer: DeserializationStrategy<T>, str: String): Res<T, KtcpErr> {
        return try {
            Res.Ok(json.decodeFromString(deserializer, str))
        } catch (e: SerializationException) {
            Res.Err(
                IllegalFormatDeserializeErr(
                    "", e
                )
            )
        } catch (e: IllegalArgumentException) {
            Res.Err(InvalidTypeDeserializeErr("", e))
        }
    }
}

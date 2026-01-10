package net.kigawa.keruta.ktcp.model.serialize

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationException
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import net.kigawa.keruta.ktcp.model.err.server.types.DeserializeErr
import net.kigawa.keruta.ktcp.model.err.server.types.IllegalFormatDeserializeErr
import net.kigawa.keruta.ktcp.model.err.server.types.InvalidTypeDeserializeErr
import net.kigawa.kodel.api.err.Res

class JsonMsgSerializer: MsgSerializer {
    val json = Json { encodeDefaults = true }
    override val serializersModule: SerializersModule
        get() = json.serializersModule

    override fun <T> serialize(serializer: SerializationStrategy<T>, value: T): String {
        return json.encodeToString(serializer, value)
    }


    override fun <T> deserialize(deserializer: DeserializationStrategy<T>, str: String): Res<T, DeserializeErr> {
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

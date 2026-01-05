package net.kigawa.keruta.ktcp.model.serialize

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import net.kigawa.keruta.ktcp.model.err.types.DeserializeErr
import net.kigawa.keruta.ktcp.model.err.types.IllegalFormatDeserializeErr
import net.kigawa.keruta.ktcp.model.err.types.InvalidTypeDeserializeErr
import net.kigawa.kodel.api.err.Res

class JsonMsgSerializer: MsgSerializer {
    override val serializersModule: SerializersModule
        get() = Json.serializersModule

    override fun serialize(msg: @Serializable Any): String {
        return Json.encodeToString(msg)
    }

    override fun <T> deserialize(deserializer: DeserializationStrategy<T>, str: String): Res<T, DeserializeErr> {
        return try {
            Res.Ok(Json.decodeFromString(deserializer, str))
        } catch (e: SerializationException) {
            Res.Err(IllegalFormatDeserializeErr(e))
        } catch (e: IllegalArgumentException) {
            Res.Err(InvalidTypeDeserializeErr(e))
        }
    }
}

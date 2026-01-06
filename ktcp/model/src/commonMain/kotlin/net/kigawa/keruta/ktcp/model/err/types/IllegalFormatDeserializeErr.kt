package net.kigawa.keruta.ktcp.model.err.types

import kotlinx.serialization.SerializationException
import net.kigawa.keruta.ktcp.model.err.ErrCode

class IllegalFormatDeserializeErr(e: SerializationException): DeserializeErr(
    ErrCode.ILLEGAL_FORMAT_DESERIALIZE,
    cause = e
) {
}

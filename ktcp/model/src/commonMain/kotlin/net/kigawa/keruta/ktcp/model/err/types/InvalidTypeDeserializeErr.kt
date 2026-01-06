package net.kigawa.keruta.ktcp.model.err.types

import net.kigawa.keruta.ktcp.model.err.ErrCode

class InvalidTypeDeserializeErr(e: IllegalArgumentException): DeserializeErr(
    ErrCode.INVALID_TYPE_DESERIALIZE,
    cause = e
)

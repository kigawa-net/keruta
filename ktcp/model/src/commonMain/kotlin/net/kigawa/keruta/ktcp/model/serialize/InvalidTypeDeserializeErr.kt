package net.kigawa.keruta.ktcp.model.serialize

import net.kigawa.keruta.ktcp.model.err.ErrCode

class InvalidTypeDeserializeErr(e: IllegalArgumentException): DeserializeErr(ErrCode.INVALID_TYPE_DESERIALIZE) {
}

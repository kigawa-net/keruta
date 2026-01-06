package net.kigawa.keruta.ktcp.model.err.types

import net.kigawa.keruta.ktcp.model.err.ErrCode

sealed class DecodeFrameErr(
    code: ErrCode, cause: DeserializeErr? = null,
): KtcpErr(code, cause = cause) {
}

package net.kigawa.keruta.ktcp.model.err.types

import net.kigawa.keruta.ktcp.model.err.ErrCode

sealed class DeserializeErr(
    code: ErrCode, cause: IllegalArgumentException? = null,
): KtcpErr(code, cause = cause)

package net.kigawa.keruta.ktcp.model.serialize

import net.kigawa.keruta.ktcp.model.err.ErrCode
import net.kigawa.keruta.ktcp.model.err.KtcpErr

sealed class DecodeFrameErr(code: ErrCode): KtcpErr(code) {
}

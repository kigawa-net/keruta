package net.kigawa.keruta.ktse.reader

import net.kigawa.keruta.ktcp.model.err.ErrCode
import net.kigawa.keruta.ktcp.model.err.KtcpErr

sealed class DecodeFrameErr(code: ErrCode): KtcpErr(code) {
}

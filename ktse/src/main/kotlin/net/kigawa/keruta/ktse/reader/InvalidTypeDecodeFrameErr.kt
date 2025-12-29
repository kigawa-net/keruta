package net.kigawa.keruta.ktse.reader

import net.kigawa.keruta.ktcp.model.err.ErrCode

class InvalidTypeDecodeFrameErr: DecodeFrameErr(ErrCode.INVALID_TYPE_DECODE_FRAME) {
}

package net.kigawa.keruta.ktse.err

import net.kigawa.keruta.ktcp.model.err.server.ServerErrCode
import net.kigawa.keruta.ktcp.server.err.KtcpServerErr

class NoSingleRecordErr(message: String?, cause: Exception?): KtcpServerErr(
    ServerErrCode.NO_SINGLE_RECORD, message,
    cause
)

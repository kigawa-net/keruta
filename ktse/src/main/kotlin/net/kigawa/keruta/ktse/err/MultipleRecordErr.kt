package net.kigawa.keruta.ktse.err

import net.kigawa.keruta.ktcp.domain.err.server.ServerErrCode
import net.kigawa.keruta.ktcp.server.err.KtcpServerErr

class MultipleRecordErr(message: String?, cause: Exception?): KtcpServerErr(
    ServerErrCode.MULTIPLE_RECORD, message,
    cause
)

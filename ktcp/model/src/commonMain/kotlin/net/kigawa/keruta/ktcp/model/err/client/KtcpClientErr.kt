package net.kigawa.keruta.ktcp.model.err.client

import net.kigawa.keruta.ktcp.model.err.KtcpErr

class KtcpClientErr(code: ClientErrCode, message: String?, cause: Exception?): KtcpErr(
    "$code: ${message ?: ""}", cause
)

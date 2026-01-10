package net.kigawa.keruta.ktcp.client.err

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.err.client.ClientErrCode

class KtcpClientErr(val errCode: ClientErrCode, message: String?, cause: Exception?): KtcpErr(
    "$errCode: ${message ?: ""}", cause
) {
    override val code: String
        get() = errCode.name
}

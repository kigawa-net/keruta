package net.kigawa.keruta.ktse.err

import net.kigawa.keruta.ktcp.model.err.GenericErrArg
import net.kigawa.keruta.ktcp.model.err.GenericErrMsg
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.server.err.KtcpServerErr

class SendGenericErrArg(
    val err: KtcpErr,
): GenericErrArg {
    override val msg: GenericErrMsg
        get() = GenericErrMsg(
            errorCode = err.code,
            errorMessage = err.message ?: "empty message"
        )
}

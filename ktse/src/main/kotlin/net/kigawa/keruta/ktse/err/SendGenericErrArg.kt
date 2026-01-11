package net.kigawa.keruta.ktse.err

import net.kigawa.keruta.ktcp.model.err.ClientGenericErrArg
import net.kigawa.keruta.ktcp.model.err.GenericErrMsg
import net.kigawa.keruta.ktcp.model.err.KtcpErr

class SendGenericErrArg(
    val err: KtcpErr,
): ClientGenericErrArg {
    override val msg: GenericErrMsg
        get() = GenericErrMsg(
            errorCode = err.code,
            errorMessage = err.message ?: "empty message"
        )
}

package net.kigawa.keruta.ktcp.model.msg

import net.kigawa.keruta.ktcp.model.auth.sccess.ClientAuthSuccessArg
import net.kigawa.keruta.ktcp.model.err.ClientGenericErrArg
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.kodel.api.err.Res

interface ClientUnknownArg {
    fun tryToGenericError(): Res<ClientGenericErrArg, KtcpErr>?
    fun tryToAuthSuccess(): Res<ClientAuthSuccessArg, KtcpErr>?

}

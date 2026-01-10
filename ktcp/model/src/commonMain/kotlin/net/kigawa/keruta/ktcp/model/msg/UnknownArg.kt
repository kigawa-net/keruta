package net.kigawa.keruta.ktcp.model.msg

import net.kigawa.keruta.ktcp.model.auth.request.ServerAuthRequestArg
import net.kigawa.keruta.ktcp.model.auth.sccess.ClientAuthSuccessArg
import net.kigawa.keruta.ktcp.model.err.GenericErrArg
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.kodel.api.err.Res

interface UnknownArg {
    fun tryToGenericError(): Res<GenericErrArg, KtcpErr>?
    fun tryToAuthenticate(): Res<ServerAuthRequestArg, KtcpErr>?
    fun tryToAuthSuccess(): Res<ClientAuthSuccessArg, KtcpErr>?
}

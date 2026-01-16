package net.kigawa.keruta.ktcp.model.msg

import net.kigawa.keruta.ktcp.model.auth.request.ServerAuthRequestArg
import net.kigawa.keruta.ktcp.model.err.ClientGenericErrArg
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.provider.request.ServerProvidersRequestArg
import net.kigawa.keruta.ktcp.model.task.ServerTaskCreateArg
import net.kigawa.kodel.api.err.Res

interface ServerUnknownArg {
    fun tryToGenericError(): Res<ClientGenericErrArg, KtcpErr>?
    fun tryToAuthenticate(): Res<ServerAuthRequestArg, KtcpErr>?
    fun tryToTaskCreate(): Res<ServerTaskCreateArg, KtcpErr>?
    fun tryToProvidersRequest(): Res<ServerProvidersRequestArg, KtcpErr>?
}

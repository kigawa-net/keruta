package net.kigawa.keruta.ktcp.server.provider

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.provider.request.ServerProvidersRequestArg
import net.kigawa.keruta.ktcp.model.provider.request.ServerProvidersRequestEntrypoint
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.keruta.ktcp.server.err.UnauthenticatedErr
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class ReceiveProvidersRequestEntrypoint: ServerProvidersRequestEntrypoint<ServerCtx> {
    override fun access(
        input: ServerProvidersRequestArg, ctx: ServerCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> {
        return EntrypointDeferred {
            val authed = ctx.session.authenticated()
                ?: return@EntrypointDeferred Res.Err(UnauthenticatedErr("", null))
            authed.persisterSession.getProviders()
            TODO()
        }
    }

}

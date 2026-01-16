package net.kigawa.keruta.ktcp.server.provider

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.provider.list.ClientProviderListMsg
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
            when (val res = authed.persisterSession.getProviders()) {
                is Res.Err -> res.x()
                is Res.Ok -> {
                    ctx.server.clientEntrypoints.providerList.access(
                        SendProviderListArg(
                            ClientProviderListMsg(
                                providers = res.value.map { it.asProviderListProvider() }
                            )
                        ), ctx
                    )
                    Res.Ok(Unit)
                }
            }
        }
    }

}

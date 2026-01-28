package net.kigawa.keruta.ktcp.server.provider

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.provider.list.ClientProviderListMsg
import net.kigawa.keruta.ktcp.model.provider.request.ServerProvidersRequestArg
import net.kigawa.keruta.ktcp.model.provider.request.ServerProvidersRequestEntrypoint
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.keruta.ktcp.server.err.ResponseErr
import net.kigawa.keruta.ktcp.server.err.UnauthenticatedErr
import net.kigawa.kodel.api.dump.dump
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.log.LoggerFactory
import net.kigawa.kodel.api.log.traceignore.debug

class ReceiveProvidersRequestEntrypoint: ServerProvidersRequestEntrypoint<ServerCtx> {
    override fun access(
        input: ServerProvidersRequestArg, ctx: ServerCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> {
        val logger = LoggerFactory.get(
            "net.kigawa.keruta.ktcp.server.provider.ReceiveProvidersRequestEntrypoint"
        )
        return EntrypointDeferred {
            val authed = ctx.session.authenticated()
                ?: return@EntrypointDeferred Res.Err(UnauthenticatedErr("", null))
            when (val res = authed.persisterSession.getProviders()) {
                is Res.Err -> res.x()
                is Res.Ok -> {
                    logger.debug("providers: ${res.value.dump}")
                    ctx.server.clientEntrypoints.providerList.access(
                        SendProviderListArg(
                            ClientProviderListMsg(
                                providers = res.value.map { it.asProviderListProvider() }
                            )
                        ), ctx
                    )?.execute() ?: Res.Err(ResponseErr("", null))
                }
            }
        }
    }

}

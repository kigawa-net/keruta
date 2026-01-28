package net.kigawa.keruta.ktcp.server.provider

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.provider.listed.ClientProviderListedMsg
import net.kigawa.keruta.ktcp.model.provider.list.ServerProviderListArg
import net.kigawa.keruta.ktcp.model.provider.list.ServerProviderListEntrypoint
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.keruta.ktcp.server.err.ResponseErr
import net.kigawa.keruta.ktcp.server.err.UnauthenticatedErr
import net.kigawa.kodel.api.dump.dump
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.log.LoggerFactory
import net.kigawa.kodel.api.log.traceignore.debug

class ReceiveProviderListEntrypoint: ServerProviderListEntrypoint<ServerCtx> {
    override fun access(
        input: ServerProviderListArg, ctx: ServerCtx,
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
                        SendProviderListedArg(
                            ClientProviderListedMsg(
                                providers = res.value.map { it.asProviderListProvider() }
                            )
                        ), ctx
                    )?.execute() ?: Res.Err(ResponseErr("", null))
                }
            }
        }
    }

}

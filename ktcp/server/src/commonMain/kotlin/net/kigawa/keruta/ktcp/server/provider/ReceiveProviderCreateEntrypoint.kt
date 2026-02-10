package net.kigawa.keruta.ktcp.server.provider

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.provider.create.ServerProviderCreateArg
import net.kigawa.keruta.ktcp.model.provider.create.ServerProviderCreateEntrypoint
import net.kigawa.keruta.ktcp.model.provider.created.ClientProviderCreatedMsg
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.keruta.ktcp.server.err.ResponseErr
import net.kigawa.keruta.ktcp.server.err.UnauthenticatedErr
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.log.LoggerFactory
import net.kigawa.kodel.api.log.traceignore.debug

class ReceiveProviderCreateEntrypoint: ServerProviderCreateEntrypoint<ServerCtx> {
    override fun access(
        input: ServerProviderCreateArg, ctx: ServerCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> {
        val logger = LoggerFactory.get(
            "net.kigawa.keruta.ktcp.server.provider.ReceiveProviderCreateEntrypoint"
        )
        return EntrypointDeferred {
            val authed = ctx.session.authenticated()
                ?: return@EntrypointDeferred Res.Err(UnauthenticatedErr("", null))

            val msg = input.msg
            logger.debug("Creating provider: name=${msg.name}, issuer=${msg.issuer}, audience=${msg.audience}")

            when (val res = authed.persisterSession.createProvider(msg.name, msg.issuer, msg.audience)) {
                is Res.Err -> res.convert()
                is Res.Ok -> {
                    logger.debug("Provider created successfully: id=${res.value.id}, name=${res.value.name}")
                    ctx.server.clientEntrypoints.providerCreated.access(
                        SendProviderCreatedArg(
                            ClientProviderCreatedMsg(
                                provider = res.value.asProviderCreatedProvider()
                            )
                        ), ctx
                    )?.execute() ?: Res.Err(ResponseErr("", null))
                }
            }
        }
    }
}

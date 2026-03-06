package net.kigawa.keruta.ktse.websocket.entrypoint

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.provider.complete.ServerProviderCompleteEntrypoint
import net.kigawa.keruta.ktcp.model.provider.complete.ServerProviderCompleteMsg
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class ReceiveProviderCompleteEntrypoint: ServerProviderCompleteEntrypoint<ServerCtx> {
    override fun access(
        input: ServerProviderCompleteMsg, ctx: ServerCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> {
        return EntrypointDeferred {
            ctx.session.registerProvider(input)
        }
    }
}

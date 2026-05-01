package net.kigawa.keruta.ktse.websocket.entrypoint

import net.kigawa.keruta.ktcp.domain.err.KtcpErr
import net.kigawa.keruta.ktcp.domain.provider.delete.ServerProviderDeleteEntrypoint
import net.kigawa.keruta.ktcp.domain.provider.delete.ServerProviderDeleteMsg
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.keruta.ktse.persist.ProviderDeleteHandler
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class ReceiveProviderDeleteEntrypoint(
    private val handler: ProviderDeleteHandler,
) : ServerProviderDeleteEntrypoint<ServerCtx> {
    override fun access(
        input: ServerProviderDeleteMsg, ctx: ServerCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> {
        return EntrypointDeferred { handler.handle(input, ctx) }
    }
}

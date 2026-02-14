package net.kigawa.keruta.ktse.websocket.entrypoint

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.provider.add.ServerProviderAddEntrypoint
import net.kigawa.keruta.ktcp.model.provider.add.ServerProviderAddMsg
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.keruta.ktse.persist.ProviderAddHandler
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class ReceiveProviderAddEntrypoint(
    private val handler: ProviderAddHandler,
) : ServerProviderAddEntrypoint<ServerCtx> {
    override fun access(
        input: ServerProviderAddMsg, ctx: ServerCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> {
        return EntrypointDeferred { handler.handle(input, ctx) }
    }
}

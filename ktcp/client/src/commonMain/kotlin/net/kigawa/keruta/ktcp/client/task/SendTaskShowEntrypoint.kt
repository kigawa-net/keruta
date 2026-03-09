package net.kigawa.keruta.ktcp.client.task

import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.client.err.KtcpClientErr
import net.kigawa.keruta.ktcp.domain.serialize.serialize
import net.kigawa.keruta.ktcp.domain.task.show.ServerTaskShowEntrypoint
import net.kigawa.keruta.ktcp.domain.task.show.ServerTaskShowMsg
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class SendTaskShowEntrypoint: ServerTaskShowEntrypoint<ClientCtx> {
    override fun access(
        input: ServerTaskShowMsg, ctx: ClientCtx,
    ): EntrypointDeferred<Res<Unit, KtcpClientErr>> = EntrypointDeferred {
        ctx.connection.send(ctx.serializer.serialize(input))
        Res.Ok(Unit)
    }
}

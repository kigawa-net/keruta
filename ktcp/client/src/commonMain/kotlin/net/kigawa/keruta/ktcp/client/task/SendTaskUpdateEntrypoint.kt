package net.kigawa.keruta.ktcp.client.task

import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.serialize.serialize
import net.kigawa.keruta.ktcp.model.task.update.ServerTaskUpdateEntrypoint
import net.kigawa.keruta.ktcp.model.task.update.ServerTaskUpdateMsg
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class SendTaskUpdateEntrypoint: ServerTaskUpdateEntrypoint<ClientCtx> {
    override fun access(
        input: ServerTaskUpdateMsg, ctx: ClientCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> {
        return EntrypointDeferred {
            ctx.connection.send(ctx.serializer.serialize(input))
            Res.Ok(Unit)
        }
    }
}
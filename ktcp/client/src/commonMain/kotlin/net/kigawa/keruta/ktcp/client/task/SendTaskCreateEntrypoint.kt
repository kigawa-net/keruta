package net.kigawa.keruta.ktcp.client.task

import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.serialize.serialize
import net.kigawa.keruta.ktcp.model.task.ServerTaskCreateArg
import net.kigawa.keruta.ktcp.model.task.ServerTaskCreateEntrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class SendTaskCreateEntrypoint: ServerTaskCreateEntrypoint<ClientCtx> {
    override fun access(
        input: ServerTaskCreateArg, ctx: ClientCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> {
       return EntrypointDeferred{
           ctx.connection.send(ctx.serializer.serialize(input.taskCreateMsg))
           Res.Ok(Unit)
       }
    }
}

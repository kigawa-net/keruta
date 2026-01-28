package net.kigawa.keruta.ktcp.server.task

import net.kigawa.keruta.ktcp.model.serialize.serialize
import net.kigawa.keruta.ktcp.model.task.updated.ClientTaskUpdatedEntrypoint
import net.kigawa.keruta.ktcp.model.task.updated.ClientTaskUpdatedMsg
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class SendTaskUpdatedEntrypoint: ClientTaskUpdatedEntrypoint<ServerCtx> {
    override fun access(
        input: ClientTaskUpdatedMsg, ctx: ServerCtx,
    ): EntrypointDeferred<Res<Unit, Nothing>> {
        return EntrypointDeferred {
            ctx.connection.send(
                ctx.serializer.serialize(input)
            )
            Res.Ok(Unit)
        }
    }
}
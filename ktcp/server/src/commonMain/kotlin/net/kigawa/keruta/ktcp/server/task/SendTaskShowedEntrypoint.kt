package net.kigawa.keruta.ktcp.server.task

import net.kigawa.keruta.ktcp.domain.serialize.serialize
import net.kigawa.keruta.ktcp.domain.task.showed.ClientTaskShowedEntrypoint
import net.kigawa.keruta.ktcp.domain.task.showed.ClientTaskShowedMsg
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class SendTaskShowedEntrypoint: ClientTaskShowedEntrypoint<ServerCtx> {
    override fun access(
        input: ClientTaskShowedMsg, ctx: ServerCtx,
    ): EntrypointDeferred<Res<Unit, Nothing>> {
        return EntrypointDeferred {
            ctx.connection.send(
                ctx.serializer.serialize(input)
            )
            Res.Ok(Unit)
        }
    }
}

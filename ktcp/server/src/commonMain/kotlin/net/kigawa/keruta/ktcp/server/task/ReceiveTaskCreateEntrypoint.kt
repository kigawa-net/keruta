package net.kigawa.keruta.ktcp.server.task

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.task.ServerTaskCreateArg
import net.kigawa.keruta.ktcp.model.task.ServerTaskCreateEntrypoint
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class ReceiveTaskCreateEntrypoint: ServerTaskCreateEntrypoint<ServerCtx> {
    override fun access(
        input: ServerTaskCreateArg, ctx: ServerCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>>? {
        TODO("Not yet implemented")
    }
}

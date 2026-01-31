package net.kigawa.keruta.ktcl.k8s.entrypoint

import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.task.moved.ClientTaskMovedEntrypoint
import net.kigawa.keruta.ktcp.model.task.moved.ClientTaskMovedMsg
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class ReceiveTaskMovedEntrypoint: ClientTaskMovedEntrypoint<ClientCtx> {
    override fun access(
        input: ClientTaskMovedMsg,
        ctx: ClientCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> = EntrypointDeferred {
        Res.Ok(Unit)
    }
}

package net.kigawa.keruta.ktcl.k8s.entrypoint

import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.task.updated.ClientTaskUpdatedEntrypoint
import net.kigawa.keruta.ktcp.model.task.updated.ClientTaskUpdatedMsg
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class ReceiveTaskUpdatedEntrypoint : ClientTaskUpdatedEntrypoint<ClientCtx> {
    override fun access(
        input: ClientTaskUpdatedMsg,
        ctx: ClientCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> = EntrypointDeferred {
        Res.Ok(Unit)
    }
}

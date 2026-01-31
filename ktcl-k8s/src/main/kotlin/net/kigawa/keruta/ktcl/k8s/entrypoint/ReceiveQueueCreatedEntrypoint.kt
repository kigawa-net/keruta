package net.kigawa.keruta.ktcl.k8s.entrypoint

import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.queue.created.ClientQueueCreatedEntrypoint
import net.kigawa.keruta.ktcp.model.queue.created.ClientQueueCreatedMsg
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class ReceiveQueueCreatedEntrypoint: ClientQueueCreatedEntrypoint<ClientCtx> {
    override fun access(
        input: ClientQueueCreatedMsg,
        ctx: ClientCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> = EntrypointDeferred {
        Res.Ok(Unit)
    }
}

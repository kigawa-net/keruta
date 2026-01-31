package net.kigawa.keruta.ktcl.k8s.entrypoint

import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.queue.listed.ClientQueueListedEntrypoint
import net.kigawa.keruta.ktcp.model.queue.listed.ClientQueueListedMsg
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class ReceiveQueueListedEntrypoint : ClientQueueListedEntrypoint<ClientCtx> {
    override fun access(
        input: ClientQueueListedMsg,
        ctx: ClientCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> = EntrypointDeferred {
        Res.Ok(Unit)
    }
}

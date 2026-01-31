package net.kigawa.keruta.ktcl.k8s.entrypoint

import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.queue.showed.ClientQueueShowedEntrypoint
import net.kigawa.keruta.ktcp.model.queue.showed.ClientQueueShowedMsg
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class ReceiveQueueShowedEntrypoint : ClientQueueShowedEntrypoint<ClientCtx> {
    override fun access(
        input: ClientQueueShowedMsg,
        ctx: ClientCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> = EntrypointDeferred {
        Res.Ok(Unit)
    }
}

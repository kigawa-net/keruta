package net.kigawa.keruta.ktcp.server.queue

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.queue.list.ServerQueueListEntrypoint
import net.kigawa.keruta.ktcp.model.queue.list.ServerQueueListMsg
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class ReceiveQueueListEntrypoint: ServerQueueListEntrypoint<ServerCtx> {
    override fun access(
        input: ServerQueueListMsg, ctx: ServerCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> {
        return EntrypointDeferred {
            TODO()
        }
    }

}

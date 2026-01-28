package net.kigawa.keruta.ktcp.model.queue.show

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.msg.server.ServerMsgType
import net.kigawa.kodel.api.entrypoint.Entrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.kodel.api.err.Res

interface ServerQueueShowEntrypoint<C>: Entrypoint<ServerQueueShowMsg, EntrypointDeferred<Res<Unit, KtcpErr>>, C> {
    override val info: EntrypointInfo
        get() = EntrypointInfo(
            ServerMsgType.QUEUE_SHOW.str, emptyList(),
            ""
        )
}

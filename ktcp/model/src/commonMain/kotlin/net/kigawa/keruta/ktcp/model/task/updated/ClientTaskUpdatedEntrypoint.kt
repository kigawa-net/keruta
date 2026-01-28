package net.kigawa.keruta.ktcp.model.task.updated

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.msg.client.ClientMsgType
import net.kigawa.kodel.api.entrypoint.Entrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.kodel.api.err.Res

interface ClientTaskUpdatedEntrypoint<C>:
    Entrypoint<ClientTaskUpdatedMsg, EntrypointDeferred<Res<Unit, KtcpErr>>, C> {
    override val info: EntrypointInfo
        get() = EntrypointInfo(
            ClientMsgType.TASK_UPDATED.str,
            emptyList(), ""
        )
}
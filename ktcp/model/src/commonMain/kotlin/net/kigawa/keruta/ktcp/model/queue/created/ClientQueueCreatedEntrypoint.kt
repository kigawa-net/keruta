package net.kigawa.keruta.ktcp.model.queue.created

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.msg.client.ClientMsgType
import net.kigawa.keruta.ktcp.model.provider.list.ClientProviderListArg
import net.kigawa.kodel.api.entrypoint.Entrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.kodel.api.err.Res

interface ClientQueueCreatedEntrypoint<C>:
    Entrypoint<ClientQueueCreatedArg, EntrypointDeferred<Res<Unit, KtcpErr>>, C> {
    override val info: EntrypointInfo
        get() = EntrypointInfo(ClientMsgType.QUEUE_CREATED.str, emptyList(), "")
}

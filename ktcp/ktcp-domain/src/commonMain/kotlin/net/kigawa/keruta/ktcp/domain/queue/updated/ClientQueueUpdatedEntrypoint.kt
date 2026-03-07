package net.kigawa.keruta.ktcp.domain.queue.updated

import net.kigawa.keruta.ktcp.domain.msg.client.ClientMsgType
import net.kigawa.kodel.api.entrypoint.Entrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.kodel.api.err.Res

interface ClientQueueUpdatedEntrypoint<C>: Entrypoint<ClientQueueUpdatedMsg, EntrypointDeferred<Res<Unit, Nothing>>, C> {
    override val info: EntrypointInfo
        get() = EntrypointInfo(
            ClientMsgType.QUEUE_UPDATED.str, emptyList(),
            "キュー更新済みメッセージ送信"
        )
}
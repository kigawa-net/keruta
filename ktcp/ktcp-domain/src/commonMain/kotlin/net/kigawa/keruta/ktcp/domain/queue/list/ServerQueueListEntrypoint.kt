package net.kigawa.keruta.ktcp.domain.queue.list

import net.kigawa.keruta.ktcp.domain.err.KtcpErr
import net.kigawa.keruta.ktcp.domain.msg.server.ServerMsgType
import net.kigawa.kodel.api.entrypoint.Entrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.kodel.api.err.Res

interface ServerQueueListEntrypoint<C>: Entrypoint<ServerQueueListMsg, EntrypointDeferred<Res<Unit, KtcpErr>>, C> {
    override val info: EntrypointInfo
        get() = EntrypointInfo(
            ServerMsgType.QUEUE_LIST.str, emptyList(),
            "キュー一覧要求メッセージ処理"
        )
}

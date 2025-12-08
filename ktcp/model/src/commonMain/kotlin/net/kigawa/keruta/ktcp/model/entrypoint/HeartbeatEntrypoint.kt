package net.kigawa.keruta.ktcp.model.entrypoint

import net.kigawa.keruta.ktcp.model.message.HeartbeatMessage
import net.kigawa.keruta.ktcp.model.message.KtcpMessage
import net.kigawa.kodel.api.entrypoint.Entrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointInfo

class HeartbeatEntrypoint: Entrypoint<HeartbeatMessage, KtcpMessage> {
    override val info = EntrypointInfo("heartbeat", emptyList(), "ハートビートメッセージ処理")

    override fun access(input: HeartbeatMessage): KtcpMessage {
        // ハートビート処理
        return input // 応答としてそのまま返す
    }
}

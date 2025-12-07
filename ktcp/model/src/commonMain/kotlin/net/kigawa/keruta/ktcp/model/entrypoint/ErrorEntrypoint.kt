package net.kigawa.keruta.ktcp.model.entrypoint

import net.kigawa.keruta.ktcp.model.message.ErrorMsg
import net.kigawa.keruta.ktcp.model.message.KtcpMessage
import net.kigawa.kodel.api.entrypoint.Entrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointInfo


interface ErrorEntrypoint: Entrypoint<ErrorMsg, KtcpMessage> {
    override val info: EntrypointInfo
        get() = EntrypointInfo("error", emptyList(), "エラーメッセージ処理")

    override fun access(input: ErrorMsg): KtcpMessage {
        // エラー処理
        return input
    }
}

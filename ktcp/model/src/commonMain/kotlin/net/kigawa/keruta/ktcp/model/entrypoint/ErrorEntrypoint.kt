package net.kigawa.keruta.ktcp.model.entrypoint

import net.kigawa.kodel.api.entrypoint.Entrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.keruta.ktcp.model.message.*





class ErrorEntrypoint : Entrypoint<ErrorMessage, KtcpMessage> {
    override val info = EntrypointInfo("error", emptyList(), "エラーメッセージ処理")

    override fun access(input: ErrorMessage): KtcpMessage {
        // エラー処理
        return input
    }
}

package net.kigawa.keruta.ktcl.claudecode.err

import net.kigawa.keruta.ktcp.domain.err.KtcpErr

sealed class ClaudeCodeErr(message: String, cause: Exception?) : KtcpErr(message, cause) {
    abstract override val code: String
}

class ClaudeApiErr(message: String, cause: Exception? = null) : ClaudeCodeErr(message, cause) {
    override val code: String = "CLAUDE_API_ERR"
}

class ConnectionErr(message: String, cause: Exception? = null) : ClaudeCodeErr(message, cause) {
    override val code: String = "CONNECTION_ERR"
}

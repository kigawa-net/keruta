package net.kigawa.keruta.ktcl.claudecode.config

data class ClaudeCodeConfig(
    val ktseHost: String,
    val ktsePort: Int,
    val ktseUseTls: Boolean,
    val userToken: String,
    val serverToken: String,
    val queueId: Long,
    val taskId: Long,
) {
    companion object {
        fun fromEnvironment(): ClaudeCodeConfig {
            return ClaudeCodeConfig(
                ktseHost = System.getenv("KTSE_HOST") ?: "localhost",
                ktsePort = System.getenv("KTSE_PORT")?.toInt() ?: 8080,
                ktseUseTls = System.getenv("KTSE_USE_TLS")?.toBoolean() ?: false,
                userToken = System.getenv("KERUTA_USER_TOKEN")
                    ?: throw IllegalStateException("KERUTA_USER_TOKEN not set"),
                serverToken = System.getenv("KERUTA_SERVER_TOKEN")
                    ?: throw IllegalStateException("KERUTA_SERVER_TOKEN not set"),
                queueId = System.getenv("KERUTA_QUEUE_ID")?.toLong()
                    ?: throw IllegalStateException("KERUTA_QUEUE_ID not set"),
                taskId = System.getenv("TASK_ID")?.toLong()
                    ?: throw IllegalStateException("TASK_ID not set"),
            )
        }
    }
}
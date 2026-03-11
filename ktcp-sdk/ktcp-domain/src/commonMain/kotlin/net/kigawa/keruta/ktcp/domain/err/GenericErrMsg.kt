package net.kigawa.keruta.ktcp.domain.err

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.domain.msg.server.ServerMsgType
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@Serializable
data class GenericErrMsg(
    val type: ServerMsgType = ServerMsgType.GENERIC_ERROR,
    val timestamp: Instant = Clock.System.now(),
    val errorCode: String,
    val errorMessage: String,
    val failedAt: Instant = Clock.System.now(),
) {
    init {
        require(type == ServerMsgType.GENERIC_ERROR) { "invalid type: ${type.name}" }
    }

}

package net.kigawa.keruta.ktcp.model.err

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.model.err.server.ServerErrCode
import net.kigawa.keruta.ktcp.model.msg.MsgType
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@Serializable
data class GenericErrMsg(
    val type: MsgType = MsgType.GENERIC_ERROR,
    val timestamp: Instant = Clock.System.now(),
    val errorCode: ServerErrCode,
    val errorMessage: String,
    val failedAt: Instant = Clock.System.now(),
) {
    init {
        require(type == MsgType.GENERIC_ERROR) { "invalid type: ${type.name}" }
    }

}

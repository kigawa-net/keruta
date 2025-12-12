package net.kigawa.keruta.ktcp.model

import net.kigawa.keruta.ktcp.model.authenticate.AuthenticateMsg
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

interface KtcpUnknownMsg {
    @OptIn(ExperimentalTime::class)
    val timestamp: Instant

    fun tryToAuthenticate(): AuthenticateMsg?
    // TODO: Implement other message types
    // fun tryToHeartbeat(): HeartbeatMsg?
    // fun tryToTaskCompleted(): TaskCompletedMsg?
    // fun tryToTaskCancel(): TaskCancelMsg?
    // fun tryToTaskCreate(): TaskCreateMsg?
    // fun tryToError(): ErrorMsg?
}

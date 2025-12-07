package net.kigawa.keruta.ktcp.model

import net.kigawa.keruta.ktcp.model.authenticate.AuthenticateMsg
import net.kigawa.keruta.ktcp.model.cancel.TaskCancelMsg
import net.kigawa.keruta.ktcp.model.compleate.TaskCompletedMsg
import net.kigawa.keruta.ktcp.model.create.TaskCreateMsg
import net.kigawa.keruta.ktcp.model.heartbeat.HeartbeatMsg
import net.kigawa.keruta.ktcp.model.message.ErrorMsg
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

interface KtcpUnknownMsg {
    @OptIn(ExperimentalTime::class)
    val timestamp: Instant

    fun tryToAuthenticate(): AuthenticateMsg?
    fun tryToHeartbeat(): HeartbeatMsg?
    fun tryToTaskCompleted(): TaskCompletedMsg?
    fun tryToTaskCancel(): TaskCancelMsg?
    fun tryToTaskCreate(): TaskCreateMsg?
    fun tryToError(): ErrorMsg?
}

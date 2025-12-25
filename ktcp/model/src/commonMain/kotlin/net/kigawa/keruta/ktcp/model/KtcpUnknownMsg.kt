package net.kigawa.keruta.ktcp.model

import net.kigawa.keruta.ktcp.model.authenticate.AuthenticateMsg

interface KtcpUnknownMsg {
    fun tryToAuthenticateMsg(): AuthenticateMsg?
    // TODO: Implement other message types
    // fun tryToHeartbeat(): HeartbeatMsg?
    // fun tryToTaskCompleted(): TaskCompletedMsg?
    // fun tryToTaskCancel(): TaskCancelMsg?
    // fun tryToTaskCreate(): TaskCreateMsg?
    // fun tryToError(): ErrorMsg?
}

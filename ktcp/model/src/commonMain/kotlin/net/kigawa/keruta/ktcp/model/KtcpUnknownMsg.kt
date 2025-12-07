package net.kigawa.keruta.ktcp.model

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

interface KtcpUnknownMsg {
    @OptIn(ExperimentalTime::class)
    val timestamp: Instant

    fun tryToAuthenticate(): AuthenticateMsg?
    fun tryToHeartbeat(): HeartbeatMsg?
    fun tryToTaskExecute(): TaskExecuteMsg?
    fun tryToTaskStatusUpdate(): TaskStatusUpdateMsg?
    fun tryToTaskLog(): TaskLogMsg?
    fun tryToTaskCompleted(): TaskCompletedMsg?
    fun tryToTaskError(): TaskErrorMsg?
    fun tryToTaskCancel(): TaskCancelMsg?
    fun tryToTaskCreate(): TaskCreateMsg?
    fun tryToTaskCreateResponse(): TaskCreateResponseMsg?
    fun tryToTaskRead(): TaskReadMsg?
    fun tryToTaskReadResponse(): TaskReadResponseMsg?
    fun tryToTaskUpdate(): TaskUpdateMsg?
    fun tryToTaskUpdateResponse(): TaskUpdateResponseMsg?
    fun tryToTaskDelete(): TaskDeleteMsg?
    fun tryToTaskDeleteResponse(): TaskDeleteResponseMsg?
    fun tryToTaskList(): TaskListMsg?
    fun tryToTaskListResponse(): TaskListResponseMsg?
    fun tryToError(): ErrorMsg?
}

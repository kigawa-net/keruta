package net.kigawa.keruta.ktcp.model

import net.kigawa.keruta.ktcp.model.entrypoint.*
import net.kigawa.keruta.ktcp.model.message.ErrorMessage
import net.kigawa.keruta.ktcp.model.message.KtcpMessage
import net.kigawa.kodel.api.entrypoint.EntrypointGroupBase
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.kodel.api.log.getLogger
import net.kigawa.kodel.api.log.traceignore.error
import kotlin.time.ExperimentalTime

class KtcpEntrypoint: EntrypointGroupBase<KtcpUnknownMsg, KtcpMessage>() {
    val logger = getLogger()
    override val info: EntrypointInfo = EntrypointInfo(
        "Keruta Task Client Protocol",
        listOf(),
        ""
    )
    val authenticateEntrypoint = add(AuthenticateEntrypoint(), { input -> this(input.tryToAuthenticate()) })
    val heartbeatEntrypoint = add(HeartbeatEntrypoint(), { input -> this(input.tryToHeartbeat()) })
    val taskExecuteEntrypoint = add(TaskExecuteEntrypoint(), { input -> this(input.tryToTaskExecute()) })
    val taskStatusUpdateEntrypoint = add(TaskStatusUpdateEntrypoint(), { input -> this(input.tryToTaskStatusUpdate()) })
    val taskLogEntrypoint = add(TaskLogEntrypoint(), { input -> this(input.tryToTaskLog()) })
    val taskCompletedEntrypoint = add(TaskCompletedEntrypoint(), { input -> this(input.tryToTaskCompleted()) })
    val taskErrorEntrypoint = add(TaskErrorEntrypoint(), { input -> this(input.tryToTaskError()) })
    val taskCancelEntrypoint = add(TaskCancelEntrypoint(), { input -> this(input.tryToTaskCancel()) })
    val taskCreateEntrypoint = add(TaskCreateEntrypoint(), { input -> this(input.tryToTaskCreate()) })
    val taskCreateResponseEntrypoint = add(TaskCreateResponseEntrypoint(), { input -> this(input.tryToTaskCreateResponse()) })
    val taskReadEntrypoint = add(TaskReadEntrypoint(), { input -> this(input.tryToTaskRead()) })
    val taskReadResponseEntrypoint = add(TaskReadResponseEntrypoint(), { input -> this(input.tryToTaskReadResponse()) })
    val taskUpdateEntrypoint = add(TaskUpdateEntrypoint(), { input -> this(input.tryToTaskUpdate()) })
    val taskUpdateResponseEntrypoint = add(TaskUpdateResponseEntrypoint(), { input -> this(input.tryToTaskUpdateResponse()) })
    val taskDeleteEntrypoint = add(TaskDeleteEntrypoint(), { input -> this(input.tryToTaskDelete()) })
    val taskDeleteResponseEntrypoint = add(TaskDeleteResponseEntrypoint(), { input -> this(input.tryToTaskDeleteResponse()) })
    val taskListEntrypoint = add(TaskListEntrypoint(), { input -> this(input.tryToTaskList()) })
    val taskListResponseEntrypoint = add(TaskListResponseEntrypoint(), { input -> this(input.tryToTaskListResponse()) })
    val errorEntrypoint = add(ErrorEntrypoint(), { input -> this(input.tryToError()) })

    @OptIn(ExperimentalTime::class)
    override fun onSubEntrypointNotFound(
        input: KtcpUnknownMsg,
    ): KtcpMessage {
        logger.error("not found entrypoint: $input")
        return ErrorMessage(
            code = "ENTRYPOINT_NOT_FOUND",
            message = "No entrypoint found for message type: $input",
            retryable = false,
            timestamp = input.timestamp
        )
    }


}

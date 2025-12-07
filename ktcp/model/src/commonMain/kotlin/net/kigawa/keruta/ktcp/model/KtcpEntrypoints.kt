package net.kigawa.keruta.ktcp.model

import net.kigawa.keruta.ktcp.model.entrypoint.*
import net.kigawa.keruta.ktcp.model.message.ErrorMsg
import net.kigawa.keruta.ktcp.model.message.KtcpMessage
import net.kigawa.kodel.api.entrypoint.EntrypointGroupBase
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.kodel.api.log.getLogger
import net.kigawa.kodel.api.log.traceignore.error
import kotlin.time.ExperimentalTime

class KtcpEntrypoints: EntrypointGroupBase<KtcpUnknownMsg, KtcpMessage>() {
    val logger = getLogger()
    override val info: EntrypointInfo = EntrypointInfo(
        "Keruta Task Client Protocol",
        listOf(),
        ""
    )
    val authenticateEntrypoint = add(object : AuthenticateEntrypoint {}, { input -> this(input.tryToAuthenticate()) })
    val heartbeatEntrypoint = add(object : HeartbeatEntrypoint {}, { input -> this(input.tryToHeartbeat()) })
    val taskExecuteEntrypoint = add(object : TaskExecuteEntrypoint {}, { input -> this(input.tryToTaskExecute()) })
    val taskStatusUpdateEntrypoint = add(object : TaskStatusUpdateEntrypoint {}, { input -> this(input.tryToTaskStatusUpdate()) })
    val taskLogEntrypoint = add(object : TaskLogEntrypoint {}, { input -> this(input.tryToTaskLog()) })
    val taskCompletedEntrypoint = add(object : TaskCompletedEntrypoint {}, { input -> this(input.tryToTaskCompleted()) })
    val taskErrorEntrypoint = add(object : TaskErrorEntrypoint {}, { input -> this(input.tryToTaskError()) })
    val taskCancelEntrypoint = add(object : TaskCancelEntrypoint {}, { input -> this(input.tryToTaskCancel()) })
    val taskCreateEntrypoint = add(object : TaskCreateEntrypoint {}, { input -> this(input.tryToTaskCreate()) })
    val taskCreateResponseEntrypoint = add(object : TaskCreateResponseEntrypoint {}, { input -> this(input.tryToTaskCreateResponse()) })
    val taskReadEntrypoint = add(object : TaskReadEntrypoint {}, { input -> this(input.tryToTaskRead()) })
    val taskReadResponseEntrypoint = add(object : TaskReadResponseEntrypoint {}, { input -> this(input.tryToTaskReadResponse()) })
    val taskUpdateEntrypoint = add(object : TaskUpdateEntrypoint {}, { input -> this(input.tryToTaskUpdate()) })
    val taskUpdateResponseEntrypoint = add(object : TaskUpdateResponseEntrypoint {}, { input -> this(input.tryToTaskUpdateResponse()) })
    val taskDeleteEntrypoint = add(object : TaskDeleteEntrypoint {}, { input -> this(input.tryToTaskDelete()) })
    val taskDeleteResponseEntrypoint = add(object : TaskDeleteResponseEntrypoint {}, { input -> this(input.tryToTaskDeleteResponse()) })
    val taskListEntrypoint = add(object : TaskListEntrypoint {}, { input -> this(input.tryToTaskList()) })
    val taskListResponseEntrypoint = add(object : TaskListResponseEntrypoint {}, { input -> this(input.tryToTaskListResponse()) })
    val errorEntrypoint = add(object : ErrorEntrypoint {}, { input -> this(input.tryToError()) })

    @OptIn(ExperimentalTime::class)
    override fun onSubEntrypointNotFound(
        input: KtcpUnknownMsg,
    ): KtcpMessage {
        logger.error("not found entrypoint: $input")
        return ErrorMsg(
            code = "ENTRYPOINT_NOT_FOUND",
            message = "No entrypoint found for message type: $input",
            retryable = false,
            timestamp = input.timestamp
        )
    }


}

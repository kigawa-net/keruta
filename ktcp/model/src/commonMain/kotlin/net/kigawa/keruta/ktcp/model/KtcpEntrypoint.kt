package net.kigawa.keruta.ktcp.model

import net.kigawa.keruta.ktcp.model.entrypoint.*
import net.kigawa.keruta.ktcp.model.message.KtcpMessage
import net.kigawa.kodel.api.entrypoint.EntrypointGroupBase
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.kodel.api.log.getLogger
import net.kigawa.kodel.api.log.traceignore.error

class KtcpEntrypoint: EntrypointGroupBase<KtcpMessage, KtcpMessage>() {
    val logger = getLogger()
    override val info: EntrypointInfo = EntrypointInfo(
        "Keruta Task Client Protocol",
        listOf(),
        ""
    )
    val authenticateEntrypoint = add(AuthenticateEntrypoint(), { input -> input })
    val heartbeatEntrypoint = add(HeartbeatEntrypoint(), { input -> input })
    val taskExecuteEntrypoint = add(TaskExecuteEntrypoint(), { input -> input })
    val taskStatusUpdateEntrypoint = add(TaskStatusUpdateEntrypoint(), { input -> input })
    val taskLogEntrypoint = add(TaskLogEntrypoint(), { input -> input })
    val taskCompletedEntrypoint = add(TaskCompletedEntrypoint(), { input -> input })
    val taskErrorEntrypoint = add(TaskErrorEntrypoint(), { input -> input })
    val taskCancelEntrypoint = add(TaskCancelEntrypoint(), { input -> input })
    val taskCreateEntrypoint = add(TaskCreateEntrypoint(), { input -> input })
    val taskCreateResponseEntrypoint = add(TaskCreateResponseEntrypoint(), { input -> input })
    val taskReadEntrypoint = add(TaskReadEntrypoint(), { input -> input })
    val taskReadResponseEntrypoint = add(TaskReadResponseEntrypoint(), { input -> input })
    val taskUpdateEntrypoint = add(TaskUpdateEntrypoint(), { input -> input })
    val taskUpdateResponseEntrypoint = add(TaskUpdateResponseEntrypoint(), { input -> input })
    val taskDeleteEntrypoint = add(TaskDeleteEntrypoint(), { input -> input })
    val taskDeleteResponseEntrypoint = add(TaskDeleteResponseEntrypoint(), { input -> input })
    val taskListEntrypoint = add(TaskListEntrypoint(), { input -> input })
    val taskListResponseEntrypoint = add(TaskListResponseEntrypoint(), { input -> input })
    val errorEntrypoint = add(ErrorEntrypoint(), { input -> input })

    override fun onSubEntrypointNotFound(
        input: KtcpMessage,
    ): KtcpMessage? {
        logger.error("not found entrypoint: $input")
        return null
    }


}

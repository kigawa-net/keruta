package net.kigawa.keruta.ktcp.model

import net.kigawa.keruta.ktcp.model.auth.request.ServerAuthRequestEntrypoint
import net.kigawa.keruta.ktcp.model.err.EntrypointNotFoundErr
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.msg.server.ServerUnknownArg
import net.kigawa.keruta.ktcp.model.provider.list.ServerProviderListEntrypoint
import net.kigawa.keruta.ktcp.model.queue.create.ServerQueueCreateEntrypoint
import net.kigawa.keruta.ktcp.model.queue.list.ServerQueueListEntrypoint
import net.kigawa.keruta.ktcp.model.queue.show.ServerQueueShowEntrypoint
import net.kigawa.keruta.ktcp.model.task.create.ServerTaskCreateEntrypoint
import net.kigawa.keruta.ktcp.model.task.list.ServerTaskListEntrypoint
import net.kigawa.keruta.ktcp.model.task.move.ServerTaskMoveEntrypoint
import net.kigawa.keruta.ktcp.model.task.show.ServerTaskShowEntrypoint
import net.kigawa.keruta.ktcp.model.task.update.ServerTaskUpdateEntrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.entrypoint.EntrypointGroupBase
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.err.whenErrOk
import net.kigawa.kodel.api.log.LoggerFactory
import net.kigawa.kodel.api.log.traceignore.error
import kotlin.time.ExperimentalTime

@Suppress("unused")
class KtcpServerEntrypoints<C>(
    authRequestEntrypoint: ServerAuthRequestEntrypoint<C>,
    taskCreateEntrypoint: ServerTaskCreateEntrypoint<C>,
    taskUpdateEntrypoint: ServerTaskUpdateEntrypoint<C>,
    taskMoveEntrypoint: ServerTaskMoveEntrypoint<C>,
    providersRequestEntrypoint: ServerProviderListEntrypoint<C>,
    queueCreateEntrypoint: ServerQueueCreateEntrypoint<C>,
    queueListEntrypoint: ServerQueueListEntrypoint<C>,
    queueShowEntrypoint: ServerQueueShowEntrypoint<C>,
    taskListEntrypoint: ServerTaskListEntrypoint<C>,
    taskShowEntrypoint: ServerTaskShowEntrypoint<C>,
): EntrypointGroupBase<ServerUnknownArg, EntrypointDeferred<Res<Unit, KtcpErr>>, C>() {
    val logger = LoggerFactory.get("net.kigawa.keruta.ktcp.model.KtcpServerEntrypoints")
    override val info: EntrypointInfo = EntrypointInfo(
        "ktcp-server",
        listOf(),
        ""
    )

    val authRequestEntrypoint = add(authRequestEntrypoint) { input ->
        input.tryToAuthenticate()?.whenErrOk(
            { EntrypointDeferred { Res.Err(it) } }
        ) {
            this(it)
        }
    }

    val taskCreateEntrypoint = add(taskCreateEntrypoint) { input ->
        input.tryToTaskCreate()?.whenErrOk(
            { EntrypointDeferred { Res.Err(it) } }
        ) {
            this(it)
        }
    }

    val taskUpdateEntrypoint = add(taskUpdateEntrypoint) { input ->
        input.tryToTaskUpdate()?.whenErrOk(
            { EntrypointDeferred { Res.Err(it) } }
        ) {
            this(it)
        }
    }

    val taskMoveEntrypoint = add(taskMoveEntrypoint) { input ->
        input.tryToTaskMove()?.whenErrOk(
            { EntrypointDeferred { Res.Err(it) } }
        ) {
            this(it)
        }
    }

    val providersRequestEntrypoint = add(providersRequestEntrypoint) { input ->
        input.tryToProvidersRequest()?.whenErrOk(
            { EntrypointDeferred { Res.Err(it) } }
        ) {
            this(it)
        }
    }

    val queueCreate = add(queueCreateEntrypoint) { input ->
        input.tryToQueueCreate()?.whenErrOk(
            { EntrypointDeferred { Res.Err(it) } }
        ) {
            this(it)
        }
    }
    val queueList = add(queueListEntrypoint) { input ->
        input.tryToQueueList()?.whenErrOk(
            { EntrypointDeferred { Res.Err(it) } }
        ) {
            this(it)
        }
    }
    val queueShow = add(queueShowEntrypoint) { input ->
        input.tryToQueueShow()?.whenErrOk(
            { EntrypointDeferred { Res.Err(it) } }
        ) {
            this(it)
        }
    }
    val taskList = add(taskListEntrypoint) { input ->
        input.tryToTaskList()?.whenErrOk(
            { EntrypointDeferred { Res.Err(it) } }
        ) {
            this(it)
        }
    }
    val taskShow = add(taskShowEntrypoint) { input ->
        input.tryToTaskShow()?.whenErrOk(
            { EntrypointDeferred { Res.Err(it) } }
        ) {
            this(it)
        }
    }

    @OptIn(ExperimentalTime::class)
    override fun onSubEntrypointNotFound(
        input: ServerUnknownArg,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> = EntrypointDeferred {
        logger.error("not found entrypoint: $input")
        Res.Err(
            EntrypointNotFoundErr(
                "No entrypoint found for message type: $input",
                null,
            )
        )
    }
}

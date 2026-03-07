package net.kigawa.keruta.ktcp.domain

import net.kigawa.keruta.ktcp.domain.auth.request.ServerAuthRequestEntrypoint
import net.kigawa.keruta.ktcp.domain.err.EntrypointNotFoundErr
import net.kigawa.keruta.ktcp.domain.err.KtcpErr
import net.kigawa.keruta.ktcp.domain.msg.server.ServerUnknownArg
import net.kigawa.keruta.ktcp.domain.provider.add.ServerProviderIssueTokenEntrypoint
import net.kigawa.keruta.ktcp.domain.provider.complete.ServerProviderCompleteEntrypoint
import net.kigawa.keruta.ktcp.domain.provider.delete.ServerProviderDeleteEntrypoint
import net.kigawa.keruta.ktcp.domain.provider.list.ServerProviderListEntrypoint
import net.kigawa.keruta.ktcp.domain.queue.create.ServerQueueCreateEntrypoint
import net.kigawa.keruta.ktcp.domain.queue.list.ServerQueueListEntrypoint
import net.kigawa.keruta.ktcp.domain.queue.show.ServerQueueShowEntrypoint
import net.kigawa.keruta.ktcp.domain.queue.update.ServerQueueUpdateEntrypoint
import net.kigawa.keruta.ktcp.domain.task.create.ServerTaskCreateEntrypoint
import net.kigawa.keruta.ktcp.domain.task.list.ServerTaskListEntrypoint
import net.kigawa.keruta.ktcp.domain.task.move.ServerTaskMoveEntrypoint
import net.kigawa.keruta.ktcp.domain.task.show.ServerTaskShowEntrypoint
import net.kigawa.keruta.ktcp.domain.task.update.ServerTaskUpdateEntrypoint
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
    providerIssueTokenEntrypoint: ServerProviderIssueTokenEntrypoint<C>,
    providerCompleteEntrypoint: ServerProviderCompleteEntrypoint<C>,
    providerDeleteEntrypoint: ServerProviderDeleteEntrypoint<C>,
    queueCreateEntrypoint: ServerQueueCreateEntrypoint<C>,
    queueListEntrypoint: ServerQueueListEntrypoint<C>,
    queueShowEntrypoint: ServerQueueShowEntrypoint<C>,
    queueUpdateEntrypoint: ServerQueueUpdateEntrypoint<C>,
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

    val providerIssueToken = add(providerIssueTokenEntrypoint) { input ->
        input.tryToProviderIssueToken()?.whenErrOk(
            { EntrypointDeferred { Res.Err(it) } }
        ) {
            this(it)
        }
    }

    val providerComplete = add(providerCompleteEntrypoint) { input ->
        input.tryToProviderComplete()?.whenErrOk(
            { EntrypointDeferred { Res.Err(it) } }
        ) {
            this(it)
        }
    }

    val providerDelete = add(providerDeleteEntrypoint) { input ->
        input.tryToProviderDelete()?.whenErrOk(
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
    val queueUpdate = add(queueUpdateEntrypoint) { input ->
        input.tryToQueueUpdate()?.whenErrOk(
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

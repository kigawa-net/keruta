package net.kigawa.keruta.ktcp.model

import net.kigawa.keruta.ktcp.model.auth.request.ServerAuthRequestEntrypoint
import net.kigawa.keruta.ktcp.model.err.EntrypointNotFoundErr
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.msg.server.ServerUnknownArg
import net.kigawa.keruta.ktcp.model.provider.list.ServerProviderListEntrypoint
import net.kigawa.keruta.ktcp.model.queue.create.ServerQueueCreateEntrypoint
import net.kigawa.keruta.ktcp.model.queue.list.ServerQueueListEntrypoint
import net.kigawa.keruta.ktcp.model.task.ServerTaskCreateEntrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.entrypoint.EntrypointGroupBase
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.err.whenErrOk
import net.kigawa.kodel.api.log.LoggerFactory
import net.kigawa.kodel.api.log.traceignore.error
import kotlin.time.ExperimentalTime

class KtcpServerEntrypoints<C>(
    authRequestEntrypoint: ServerAuthRequestEntrypoint<C>,
    taskCreateEntrypoint: ServerTaskCreateEntrypoint<C>,
    providersRequestEntrypoint: ServerProviderListEntrypoint<C>,
    queueCreateEntrypoint: ServerQueueCreateEntrypoint<C>,
    queueListEntrypoint: ServerQueueListEntrypoint<C>,
): EntrypointGroupBase<ServerUnknownArg, EntrypointDeferred<Res<Unit, KtcpErr>>, C>() {
    val logger = LoggerFactory.get("net.kigawa.keruta.ktcp.model.KtcpServerEntrypoints")
    override val info: EntrypointInfo = EntrypointInfo(
        "ktcp-server",
        listOf(),
        ""
    )

    @Suppress("unused")
    val authRequestEntrypoint = add(authRequestEntrypoint) { input ->
        input.tryToAuthenticate()?.whenErrOk(
            { EntrypointDeferred { Res.Err(it) } }
        ) {
            this(it)
        }
    }

    @Suppress("unused")
    val taskCreateEntrypoint = add(taskCreateEntrypoint) { input ->
        input.tryToTaskCreate()?.whenErrOk(
            { EntrypointDeferred { Res.Err(it) } }
        ) {
            this(it)
        }
    }

    @Suppress("unused")
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

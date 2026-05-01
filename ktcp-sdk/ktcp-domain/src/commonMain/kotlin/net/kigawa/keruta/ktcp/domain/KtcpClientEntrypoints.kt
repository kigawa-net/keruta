package net.kigawa.keruta.ktcp.domain

import net.kigawa.keruta.ktcp.domain.auth.sccess.ClientAuthSuccessEntrypoint
import net.kigawa.keruta.ktcp.domain.err.ClientGenericErrEntrypoint
import net.kigawa.keruta.ktcp.domain.err.EntrypointNotFoundErr
import net.kigawa.keruta.ktcp.domain.err.KtcpErr
import net.kigawa.keruta.ktcp.domain.msg.client.ClientUnknownArg
import net.kigawa.keruta.ktcp.domain.provider.add_token.ClientProviderAddTokenEntrypoint
import net.kigawa.keruta.ktcp.domain.provider.deleted.ClientProviderDeletedEntrypoint
import net.kigawa.keruta.ktcp.domain.provider.idp_added.ClientProviderIdpAddedEntrypoint
import net.kigawa.keruta.ktcp.domain.provider.listed.ClientProviderListedEntrypoint
import net.kigawa.keruta.ktcp.domain.queue.created.ClientQueueCreatedEntrypoint
import net.kigawa.keruta.ktcp.domain.queue.deleted.ClientQueueDeletedEntrypoint
import net.kigawa.keruta.ktcp.domain.queue.listed.ClientQueueListedEntrypoint
import net.kigawa.keruta.ktcp.domain.queue.showed.ClientQueueShowedEntrypoint
import net.kigawa.keruta.ktcp.domain.queue.updated.ClientQueueUpdatedEntrypoint
import net.kigawa.keruta.ktcp.domain.task.created.ClientTaskCreatedEntrypoint
import net.kigawa.keruta.ktcp.domain.task.listed.ClientTaskListedEntrypoint
import net.kigawa.keruta.ktcp.domain.task.moved.ClientTaskMovedEntrypoint
import net.kigawa.keruta.ktcp.domain.task.showed.ClientTaskShowedEntrypoint
import net.kigawa.keruta.ktcp.domain.task.updated.ClientTaskUpdatedEntrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.entrypoint.EntrypointGroupBase
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.err.whenErrOk
import net.kigawa.kodel.api.log.LoggerFactory
import net.kigawa.kodel.api.log.traceignore.error
import kotlin.time.ExperimentalTime

@Suppress("unused")
class KtcpClientEntrypoints<C>(
    genericErrEntrypoint: ClientGenericErrEntrypoint<C>,
    authSuccessEntrypoint: ClientAuthSuccessEntrypoint<C>,
    providerListEntrypoint: ClientProviderListedEntrypoint<C>,
    providerAddTokenEntrypoint: ClientProviderAddTokenEntrypoint<C>,
    providerIdpAddedEntrypoint: ClientProviderIdpAddedEntrypoint<C>,
    providerDeletedEntrypoint: ClientProviderDeletedEntrypoint<C>,
    queueCreatedEntrypoint: ClientQueueCreatedEntrypoint<C>,
    queueListedEntrypoint: ClientQueueListedEntrypoint<C>,
    queueShowedEntrypoint: ClientQueueShowedEntrypoint<C>,
    queueUpdatedEntrypoint: ClientQueueUpdatedEntrypoint<C>,
    queueDeletedEntrypoint: ClientQueueDeletedEntrypoint<C>,
    taskCreatedEntrypoint: ClientTaskCreatedEntrypoint<C>,
    taskUpdatedEntrypoint: ClientTaskUpdatedEntrypoint<C>,
    taskMovedEntrypoint: ClientTaskMovedEntrypoint<C>,
    taskListedEntrypoint: ClientTaskListedEntrypoint<C>,
    taskShowedEntrypoint: ClientTaskShowedEntrypoint<C>,
): EntrypointGroupBase<ClientUnknownArg, EntrypointDeferred<Res<Unit, KtcpErr>>, C>() {
    val logger = LoggerFactory.get("net.kigawa.keruta.ktcp.model.KtcpClientEntrypoints")
    override val info: EntrypointInfo = EntrypointInfo(
        "ktcp-client",
        listOf(),
        ""
    )
    val genericError = add(
        genericErrEntrypoint
    ) { input ->
        input.tryToGenericError()?.whenErrOk(
            { EntrypointDeferred { Res.Err(it) } }
        ) {
            this(it)
        }
    }
    val authSuccess = add(authSuccessEntrypoint) { input ->
        input.tryToAuthSuccess()?.whenErrOk(
            { EntrypointDeferred { Res.Err(it) } }
        ) {
            this(it)
        }
    }
    val providerList = add(providerListEntrypoint) { input ->
        input.tryToProviderList()?.whenErrOk(
            { EntrypointDeferred { Res.Err(it) } }
        ) {
            this(it)
        }
    }

    val providerAddToken = add(providerAddTokenEntrypoint) { input ->
        input.tryToProviderAddToken()?.whenErrOk(
            { EntrypointDeferred { Res.Err(it) } }
        ) {
            this(it)
        }
    }

    val providerIdpAdded = add(providerIdpAddedEntrypoint) { input ->
        input.tryToProviderIdpAdded()?.whenErrOk(
            { EntrypointDeferred { Res.Err(it) } }
        ) {
            this(it)
        }
    }

    val providerDeleted = add(providerDeletedEntrypoint) { input ->
        input.tryToProviderDeleted()?.whenErrOk(
            { EntrypointDeferred { Res.Err(it) } }
        ) {
            this(it)
        }
    }

    val queueCreated = add(queueCreatedEntrypoint) { input ->
        input.tryToQueueCreated()?.whenErrOk(
            { EntrypointDeferred { Res.Err(it) } }
        ) {
            this(it)
        }
    }
    val queueListed = add(queueListedEntrypoint) { input ->
        input.tryToQueueListed()?.whenErrOk(
            { EntrypointDeferred { Res.Err(it) } }
        ) {
            this(it)
        }
    }
    val queueShowed = add(queueShowedEntrypoint) { input ->
        input.tryToQueueShowed()?.whenErrOk(
            { EntrypointDeferred { Res.Err(it) } }
        ) {
            this(it)
        }
    }
    val queueUpdated = add(queueUpdatedEntrypoint) { input ->
        input.tryToQueueUpdated()?.whenErrOk(
            { EntrypointDeferred { Res.Err(it) } }
        ) {
            this(it)
        }
    }
    val queueDeleted = add(queueDeletedEntrypoint) { input ->
        input.tryToQueueDeleted()?.whenErrOk(
            { EntrypointDeferred { Res.Err(it) } }
        ) {
            this(it)
        }
    }
    val taskCreated = add(taskCreatedEntrypoint) { input ->
        input.tryToTaskCreated()?.whenErrOk(
            { EntrypointDeferred { Res.Err(it) } }
        ) {
            this(it)
        }
    }
    val taskUpdated = add(taskUpdatedEntrypoint) { input ->
        input.tryToTaskUpdated()?.whenErrOk(
            { EntrypointDeferred { Res.Err(it) } }
        ) {
            this(it)
        }
    }
    val taskMoved = add(taskMovedEntrypoint) { input ->
        input.tryToTaskMoved()?.whenErrOk(
            { EntrypointDeferred { Res.Err(it) } }
        ) {
            this(it)
        }
    }
    val taskShowed = add(taskShowedEntrypoint) { input ->
        input.tryToTaskShowed()?.whenErrOk(
            { EntrypointDeferred { Res.Err(it) } }
        ) {
            this(it)
        }
    }
    val taskListed = add(taskListedEntrypoint) { input ->
        input.tryToTaskListed()?.whenErrOk(
            { EntrypointDeferred { Res.Err(it) } }
        ) {
            this(it)
        }
    }

    @OptIn(ExperimentalTime::class)
    override fun onSubEntrypointNotFound(
        input: ClientUnknownArg,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> {
        logger.error("not found entrypoint: $input")
        return EntrypointDeferred {
            Res.Err(
                EntrypointNotFoundErr(
                    "No entrypoint found for message type: $input",
                    null
                )
            )
        }
    }


}

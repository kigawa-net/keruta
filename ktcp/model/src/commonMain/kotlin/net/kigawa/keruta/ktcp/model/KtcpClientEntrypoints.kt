package net.kigawa.keruta.ktcp.model

import net.kigawa.keruta.ktcp.model.auth.sccess.ClientAuthSuccessEntrypoint
import net.kigawa.keruta.ktcp.model.err.ClientGenericErrEntrypoint
import net.kigawa.keruta.ktcp.model.err.EntrypointNotFoundErr
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.msg.client.ClientUnknownArg
import net.kigawa.keruta.ktcp.model.provider.listed.ClientProviderListedEntrypoint
import net.kigawa.keruta.ktcp.model.queue.created.ClientQueueCreatedEntrypoint
import net.kigawa.keruta.ktcp.model.queue.listed.ClientQueueListedEntrypoint
import net.kigawa.keruta.ktcp.model.queue.showed.ClientQueueShowedEntrypoint
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
    queueCreatedEntrypoint: ClientQueueCreatedEntrypoint<C>,
    queueListedEntrypoint: ClientQueueListedEntrypoint<C>,
    queueShowedEntrypoint: ClientQueueShowedEntrypoint<C>,
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

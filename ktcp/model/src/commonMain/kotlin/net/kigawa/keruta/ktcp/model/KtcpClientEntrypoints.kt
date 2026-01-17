package net.kigawa.keruta.ktcp.model

import net.kigawa.keruta.ktcp.model.auth.sccess.ClientAuthSuccessEntrypoint
import net.kigawa.keruta.ktcp.model.err.ClientGenericErrEntrypoint
import net.kigawa.keruta.ktcp.model.err.EntrypointNotFoundErr
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.msg.ClientUnknownArg
import net.kigawa.keruta.ktcp.model.provider.list.ClientProviderListEntrypoint
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
    providerListEntrypoint: ClientProviderListEntrypoint<C>,
): EntrypointGroupBase<ClientUnknownArg, EntrypointDeferred< Res<Unit, KtcpErr>>, C>() {
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
    val authSuccess = add(authSuccessEntrypoint){ input->
        input.tryToAuthSuccess()?.whenErrOk(
            { EntrypointDeferred { Res.Err(it) } }
        ) {
            this(it)
        }
    }
    val providerList = add(providerListEntrypoint){ input->
        input.tryToProviderList()?.whenErrOk(
            { EntrypointDeferred { Res.Err(it) } }
        ) {
            this(it)
        }
    }


    @OptIn(ExperimentalTime::class)
    override fun onSubEntrypointNotFound(
        input: ClientUnknownArg,
    ): EntrypointDeferred< Res<Unit, KtcpErr>> {
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

package net.kigawa.keruta.ktcp.model

import net.kigawa.keruta.ktcp.model.err.GenericErrEntrypoint
import net.kigawa.keruta.ktcp.model.msg.UnknownArg
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.entrypoint.EntrypointGroupBase
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.log.LoggerFactory
import net.kigawa.kodel.api.log.traceignore.error
import kotlin.time.ExperimentalTime

@Suppress("unused")
class KtcpClientEntrypoints<C>(
    genericErrEntrypoint: GenericErrEntrypoint<C>,
): EntrypointGroupBase<UnknownArg, EntrypointDeferred<in Res<Unit, Nothing>>, C>() {
    val logger = LoggerFactory.get("net.kigawa.keruta.ktcp.model.KtcpClientEntrypoints")
    override val info: EntrypointInfo = EntrypointInfo(
        "ktcp-client",
        listOf(),
        ""
    )
    val genericError = add(genericErrEntrypoint) { input -> input.tryToGenericError()?.let { this(it) } }


    @OptIn(ExperimentalTime::class)
    override fun onSubEntrypointNotFound(
        input: UnknownArg,
    ): EntrypointDeferred<in Res<Unit, Nothing>> {
        logger.error("not found entrypoint: $input")
        return EntrypointDeferred {
            Res.Err(
                EntrypointNotFoundErr(
                    message = "No entrypoint found for message type: $input",
                )
            )
        }
    }


}

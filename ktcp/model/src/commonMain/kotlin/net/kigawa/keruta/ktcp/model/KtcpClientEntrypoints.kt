package net.kigawa.keruta.ktcp.model

import net.kigawa.keruta.ktcp.model.err.GenericErrEntrypoint
import net.kigawa.keruta.ktcp.model.msg.UnknownArg
import net.kigawa.kodel.api.entrypoint.EntrypointGroupBase
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.log.getKogger
import net.kigawa.kodel.api.log.traceignore.error
import kotlin.time.ExperimentalTime

@Suppress("unused")
class KtcpClientEntrypoints<C>(
    genericErrEntrypoint: GenericErrEntrypoint<C>,
): EntrypointGroupBase<UnknownArg, Res<Unit, EntrypointNotFoundErr>, C>() {
    val logger = getKogger()
    override val info: EntrypointInfo = EntrypointInfo(
        "ktcp-client",
        listOf(),
        ""
    )
    val genericError = add(genericErrEntrypoint) { input -> input.tryToGenericError()?.let { this(it) } }


    @OptIn(ExperimentalTime::class)
    override fun onSubEntrypointNotFound(
        input: UnknownArg,
    ): Res<Unit, EntrypointNotFoundErr> {
        logger.error("not found entrypoint: $input")
        return Res.Err(
            EntrypointNotFoundErr(
                message = "No entrypoint found for message type: $input",
            )
        )
    }


}

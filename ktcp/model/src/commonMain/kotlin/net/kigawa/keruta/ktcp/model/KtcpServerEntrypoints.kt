package net.kigawa.keruta.ktcp.model

import net.kigawa.keruta.ktcp.model.authenticate.AuthenticateEntrypoint
import net.kigawa.keruta.ktcp.model.msg.UnknownArg
import net.kigawa.kodel.api.entrypoint.EntrypointGroupBase
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.log.getLogger
import net.kigawa.kodel.api.log.traceignore.error
import kotlin.time.ExperimentalTime

@Suppress("unused")
class KtcpServerEntrypoints<C>(
    authenticateEntrypoint: AuthenticateEntrypoint<C>,
): EntrypointGroupBase<UnknownArg, Res<Unit, EntrypointNotFoundErr>, C>() {
    val logger = getLogger()
    override val info: EntrypointInfo = EntrypointInfo(
        "ktcp-server",
        listOf(),
        ""
    )
    val authenticateEntrypoint = add(authenticateEntrypoint) { input -> this(input.tryToAuthenticate()) }


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

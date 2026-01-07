package net.kigawa.keruta.ktcp.model

import net.kigawa.keruta.ktcp.model.auth.request.AuthRequestEntrypoint
import net.kigawa.keruta.ktcp.model.err.types.EntrypointNotFoundErr
import net.kigawa.keruta.ktcp.model.err.types.KtcpErr
import net.kigawa.keruta.ktcp.model.msg.UnknownArg
import net.kigawa.kodel.api.entrypoint.EntrypointGroupBase
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.err.flatNullableOk
import net.kigawa.kodel.api.log.LoggerFactory
import net.kigawa.kodel.api.log.traceignore.error
import kotlin.time.ExperimentalTime

@Suppress("unused")
class KtcpServerEntrypoints<C>(
    authRequestEntrypoint: AuthRequestEntrypoint<C>,
): EntrypointGroupBase<UnknownArg, Res<Unit, KtcpErr>, C>() {
    val logger = LoggerFactory.get("net.kigawa.keruta.ktcp.model.KtcpServerEntrypoints")
    override val info: EntrypointInfo = EntrypointInfo(
        "ktcp-server",
        listOf(),
        ""
    )
    val authenticateEntrypoint = add(authRequestEntrypoint) { input ->
        input.tryToAuthenticate()?.flatNullableOk { this(it) }
    }


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

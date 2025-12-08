package net.kigawa.keruta.ktcp.model

import net.kigawa.keruta.ktcp.model.authenticate.AuthenticateEntrypoint
import net.kigawa.keruta.ktcp.model.err.KtcpErrRes
import net.kigawa.kodel.api.entrypoint.EntrypointGroupBase
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.kodel.api.log.getLogger
import net.kigawa.kodel.api.log.traceignore.error
import kotlin.time.ExperimentalTime

class KtcpEntrypoints(
    authenticateEntrypoint: AuthenticateEntrypoint,
): EntrypointGroupBase<KtcpUnknownMsg, KtcpRes>() {
    val logger = getLogger()
    override val info: EntrypointInfo = EntrypointInfo(
        "Keruta Task Client Protocol",
        listOf(),
        ""
    )
    val authenticateEntrypoint = add(authenticateEntrypoint, { input -> this(input.tryToAuthenticate()) })


    @OptIn(ExperimentalTime::class)
    override fun onSubEntrypointNotFound(
        input: KtcpUnknownMsg,
    ): KtcpRes {
        logger.error("not found entrypoint: $input")
        return KtcpErrRes(
            code = "ENTRYPOINT_NOT_FOUND",
            message = "No entrypoint found for message type: $input",
            retryable = false,
            timestamp = input.timestamp
        )
    }


}

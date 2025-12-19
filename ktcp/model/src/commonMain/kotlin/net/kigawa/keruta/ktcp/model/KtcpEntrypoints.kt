package net.kigawa.keruta.ktcp.model

import net.kigawa.keruta.ktcp.model.authenticate.AuthenticateEntrypoint
import net.kigawa.keruta.ktcp.model.err.ErrCode
import net.kigawa.keruta.ktcp.model.err.KtcpErrRes
import net.kigawa.kodel.api.entrypoint.EntrypointGroupBase
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.kodel.api.log.getLogger
import net.kigawa.kodel.api.log.traceignore.error
import kotlin.time.ExperimentalTime

@Suppress("unused")
class KtcpEntrypoints<C>(
    authenticateEntrypoint: AuthenticateEntrypoint<C>,
): EntrypointGroupBase<KtcpUnknownMsg, KtcpRes, C>() {
    val logger = getLogger()
    override val info: EntrypointInfo = EntrypointInfo(
        "ktcp",
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
            code = ErrCode.ENTRYPOINT_NOT_FOUND,
            message = "No entrypoint found for message type: $input",
            retryable = false,
            timestamp = input.timestamp
        )
    }


}

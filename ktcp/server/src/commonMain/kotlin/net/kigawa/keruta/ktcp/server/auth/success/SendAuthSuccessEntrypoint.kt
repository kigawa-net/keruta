package net.kigawa.keruta.ktcp.server.auth.success

import net.kigawa.keruta.ktcp.model.auth.sccess.ClientAuthSuccessEntrypoint
import net.kigawa.keruta.ktcp.model.auth.sccess.ClientAuthSuccessMsg
import net.kigawa.keruta.ktcp.model.serialize.serialize
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.log.LoggerFactory
import net.kigawa.kodel.api.log.traceignore.debug

class SendAuthSuccessEntrypoint: ClientAuthSuccessEntrypoint<ServerCtx> {
    val logger = LoggerFactory.get("net.kigawa.keruta.ktcp.server.auth.success.AuthSuccessSendEntrypoint")
    override fun access(
        input: ClientAuthSuccessMsg, ctx: ServerCtx,
    ): EntrypointDeferred<Res<Unit, Nothing>> {
        return EntrypointDeferred {
            ctx.connection.send(
                ctx.serializer.serialize(input)
                    .also { logger.debug("send auth success message: $it") }
            )
            Res.Ok(Unit)
        }
    }
}